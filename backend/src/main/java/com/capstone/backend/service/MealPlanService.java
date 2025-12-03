package com.capstone.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.gpt.MealGptClient;
import com.capstone.backend.gpt.MealPromptBuilder;
import com.capstone.backend.gpt.MealResponseParser;
import com.capstone.backend.dto.FoodCandidate;
import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.dto.FoodWithIntake;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.MealResponse;
import com.capstone.backend.dto.ResolvedMealResponse;
import com.capstone.backend.dto.WeeklyMealsResponse;
import com.capstone.backend.mapper.MealMapper;
import com.capstone.backend.service.HealthInfoService;
import com.capstone.backend.service.MealAssembler;
import com.capstone.backend.repository.DailyMealsRepository;
import com.capstone.backend.repository.MealRecommendationLogRepository;
import com.capstone.backend.domain.MealRecommendationLog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealGptClient gptClient;
    private final FoodService foodService;
    private final MealAssembler mealAssembler;
    private final HealthInfoService healthInfoService;
    private final MealRecommendationLogRepository logRepository;
    private final DailyMealsRepository dailyMealsRepository;

    public boolean hasExistingMealPlan(String userId) {
        LocalDate today = LocalDate.now();
        return logRepository.existsByUserIdAndDate(userId, today);
    }

    public void createAndSaveWeeklyMeal(String userId) {
        // 1. 사용자 건강정보 조회
        HealthInfo info = healthInfoService.getHealthInfoByUserId(userId);
        if (info == null) {
            throw new NoSuchElementException("해당 사용자 건강정보가 존재하지 않습니다: " + userId);
        }

        // 2. GPT 호출용 DTO 생성
        HealthInfoRequest request = new HealthInfoRequest(info);

        // 3. GPT에게 1주일치 식단 요청
        WeeklyMealsResponse weeklyResponse = gptClient.requestWeeklyMealPlan(request);

        // 4. 응답에서 날짜별 식단을 추출하여 저장 (logRepository에만 저장)
        Map<String, DailyMealsResponse> weeklyMeals = weeklyResponse.getMeals();

        for (Map.Entry<String, DailyMealsResponse> entry : weeklyMeals.entrySet()) {
            String dateStr = entry.getKey();
            DailyMealsResponse dto = entry.getValue();

            LocalDate date = LocalDate.parse(dateStr);

            // MealRecommendationLog에 전체 식단 저장
            MealRecommendationLog log = new MealRecommendationLog(
                userId,
                date,
                dto.getBreakfast(),
                dto.getLunch(),
                dto.getDinner()
            );

            logRepository.save(log);
        }
    }



    public Map<String, DailyMeals> loadSavedWeeklyMeals(String userId) {
    // 오늘부터 7일치(오늘~6일후)만 반환
    LocalDate today = LocalDate.now();
    LocalDate lastDay = today.plusDays(6);
    List<MealRecommendationLog> logs = logRepository.findByUserIdAndDateAfter(userId, today.minusDays(1));

    // 오늘~6일후 사이의 데이터만 필터링
    List<MealRecommendationLog> filteredLogs = logs.stream()
        .filter(log -> !log.getDate().isBefore(today) && !log.getDate().isAfter(lastDay))
        .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
        .collect(Collectors.toList());

    log.info("[DB] filtered {} MealRecommendationLog rows for userId={} ({} ~ {})", filteredLogs.size(), userId, today, lastDay);
    List<String> dates = filteredLogs.stream()
        .map(log -> log.getDate().toString())
        .collect(Collectors.toList());
    log.info("[DB] dates raw = {}", dates);

    Map<String, Long> dateHistogram = filteredLogs.stream()
        .collect(Collectors.groupingBy(
            log -> log.getDate().toString(), LinkedHashMap::new, Collectors.counting()));
    log.info("[DB] date histogram = {}", dateHistogram);

    // MealRecommendationLog -> DailyMeals 변환
    return filteredLogs.stream()
        .collect(Collectors.toMap(
            log -> log.getDate().toString(),
            log -> {
                DailyMeals daily = new DailyMeals();
                daily.setBreakfast(log.getBreakfast());
                daily.setLunch(log.getLunch());
                daily.setDinner(log.getDinner());
                daily.setDate(log.getDate().toString());
                return daily;
            },
            (existing, replacement) -> replacement,
            LinkedHashMap::new
        ));
    }


    public MealResponse getTodayMeal(String userId) {
        LocalDate today = LocalDate.now();
        // log.info("[getTodayMeal] 오늘 날짜: {}", today);
        MealRecommendationLog recommendLog = logRepository.findByUserIdAndDate(userId, today)
            .orElseThrow(() -> new NoSuchElementException("오늘 식단 없음: " + userId));

        // log.info("[getTodayMeal] userId={}의 추천 로그 조회 성공", userId);

        // rice/soup/sideDishes 분할
        List<FoodWithIntake> selected = recommendLog.getBreakfast(); // 기본값: 아침
        java.time.LocalTime now = java.time.LocalTime.now();
        // log.info("[getTodayMeal] 현재 시각: {}", now);
        if (now.isBefore(java.time.LocalTime.of(10, 0))) {
            selected = recommendLog.getBreakfast();
            // log.info("[getTodayMeal] 아침 식단 선택");
        } else if (now.isBefore(java.time.LocalTime.of(16, 0))) {
            selected = recommendLog.getLunch();
            // log.info("[getTodayMeal] 점심 식단 선택");
        } else {
            selected = recommendLog.getDinner();
            // log.info("[getTodayMeal] 저녁 식단 선택");
        }

        // log.info("[getTodayMeal] 선택된 식단: {}", selected.stream().map(FoodWithIntake::getName).toList());

        // rice/soup/sideDishes 분할
        Food rice = foodService.findByName(selected.get(0).getName());
        // log.info("[getTodayMeal] 밥: {}", rice.getName());
        Food soup = foodService.findByName(selected.get(1).getName());
        // log.info("[getTodayMeal] 국: {}", soup.getName());
        List<Food> sides = selected.subList(2, selected.size()).stream()
            .map(f -> foodService.findByName(f.getName()))
            .collect(Collectors.toList());
        // log.info("[getTodayMeal] 반찬: {}", sides.stream().map(Food::getName).toList());

        MealResponse response = new MealResponse(
            MealMapper.toResponse(rice),
            MealMapper.toResponse(soup),
            sides.stream().map(MealMapper::toResponse).collect(Collectors.toList())
        );
        // log.info("[getTodayMeal] MealResponse 생성 완료");
        return response;
    }

    public MealResponse generateMealPlan(HealthInfoRequest userInfo) {
        DailyMealsResponse dailyNames = gptClient.requestMealPlan(userInfo);
        List<String> allNames = collectAllNames(dailyNames);
        List<Food> foods = foodService.findByNames(allNames);
        ResolvedMealResponse resolved = mealAssembler.assemble(dailyNames, foods);
        return MealMapper.groupMeal(resolved.getAllFoods());
    }

    private List<String> collectAllNames(DailyMealsResponse meals) {
    List<String> all = new ArrayList<>();
    all.addAll(extractNames(meals.getBreakfast()));
    all.addAll(extractNames(meals.getLunch()));
    all.addAll(extractNames(meals.getDinner()));
    return all;
    }

    private List<String> extractNames(List<FoodWithIntake> items) {
        return items.stream()
                    .map(FoodWithIntake::getName)
                    .collect(Collectors.toList());
    }
}


