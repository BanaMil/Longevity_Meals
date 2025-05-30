package com.capstone.backend.service;

import com.capstone.backend.analysis.NutrientTargetCalculator;
import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.domain.Food;
import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.MealRecommendationLog;

import com.capstone.backend.utils.MealPlanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanService {
    private final HealthInfoService healthInfoService;
    private final FoodService foodService;
    private final NutrientTargetCalculator nutrientTargetCalculator;
    private final RecentRecommendationLogService recentRecommendationLogService;
    /**
     * 한 끼 식단을 구성합니다: 밥 1개, 국 1개, 반찬 3개
     *
     * @param allFoods       필터링된 전체 음식 목록
     * @param targetPerMeal  1끼 기준 영양소 목표값
     * @return 구성된 Food 리스트 (1끼)
     */
    public List<Food> generateOneMeal(
        List<Food> allFoods,
        Map<String, Double> targetPerMeal,
        Set<String> allergies,
        Set<String> dislikes,
        Set<String> recentFoods
) {
    // ✅ 1. 명확한 분류 기준 정의
    final Set<String> RICE_CATEGORIES = Set.of("밥류", "죽 및 스프류");
    final Set<String> SOUP_CATEGORIES = Set.of("국 및 탕류", "찌개 및 전골류");

    // ✅ 2. 카테고리 분할
    List<Food> riceList = allFoods.stream()
        .filter(f -> f.getCategory() != null && RICE_CATEGORIES.contains(f.getCategory()))
        .collect(Collectors.toList());
    log.info("[식단 생성중] 밥 후보 개수: {}", riceList.size());

    List<Food> soupList = allFoods.stream()
        .filter(f -> f.getCategory() != null && SOUP_CATEGORIES.contains(f.getCategory()))
        .collect(Collectors.toList());
    log.info("[식단 생성중] 국 후보 개수: {}", soupList.size());

    List<Food> sideList = allFoods.stream()
        .filter(f -> f.getCategory() != null &&
            !RICE_CATEGORIES.contains(f.getCategory()) &&
            !SOUP_CATEGORIES.contains(f.getCategory()))
        .collect(Collectors.toList());
    log.info("[식단 생성중] 반찬 후보 개수: {}", sideList.size());

    // 3. 현재 영양소 누적량
    Map<String, Double> current = new HashMap<>();

    // 4. 추천 음식 선택 (기여도 + softmax 기반)
    Food rice = MealPlanner.chooseMeal(riceList, current, targetPerMeal, 1, 5, allergies, dislikes, recentFoods).get(0);
    updateCurrentNutrients(current, rice);
    log.info("[식단 생성 결과] 밥: {}", rice.getName());

    Food soup = MealPlanner.chooseMeal(soupList, current, targetPerMeal, 1, 5, allergies, dislikes, recentFoods).get(0);
    updateCurrentNutrients(current, soup);
    log.info("[식단 생성 결과] 국: {}", soup.getName());

    List<Food> sides = MealPlanner.chooseMeal(sideList, current, targetPerMeal, 3, 10, allergies, dislikes, recentFoods);
    sides.forEach(f -> updateCurrentNutrients(current, f));
    log.info("[식단 생성 결과] 반찬: {}", sides.stream().map(Food::getName).toList());

    // 5. 최종 구성
    List<Food> meal = new ArrayList<>();
    meal.add(rice);
    meal.add(soup);
    meal.addAll(sides);

    return meal;
}


    /**
     * 선택된 음식의 영양소 값을 current 누적량에 반영
     */
    private void updateCurrentNutrients(Map<String, Double> current, Food food) {
        if (food == null || food.getNutrients() == null) return;

        for (Map.Entry<String, Double> entry : food.getNutrients().entrySet()) {
            String nutrient = entry.getKey();
            Double amount = entry.getValue();

            if (nutrient != null && amount != null) {
                current.put(nutrient, current.getOrDefault(nutrient, 0.0) + amount);
            }
        }
    }

        /**
     * 사용자 ID를 기반으로 한 끼 식단 추천 (상위 서비스 메서드)
     */
    public Map<String, DailyMeals> recommendMealForUser(String userId) {
        log.info("=== [식단 추천 시작] userId: {} ===", userId);
        // 1. 사용자 건강 정보 조회
        HealthInfo healthInfo = healthInfoService.getHealthInfoByUserId(userId);
        Set<String> allergies = new HashSet<>(healthInfo.getAllergies());
        Set<String> dislikes = new HashSet<>(healthInfo.getDislikes());

        // 2. 최근 2일 추천 음식 조회
        Set<String> recentFoods = recentRecommendationLogService.getLastTwoDaysFoodNames(userId);

        // 3. 전체 음식 목록 가져오기
        List<Food> allFoods = foodService.fetchFilteredFoods();

        // 4. 사용자 맞춤 영양소 목표 계산 (하루 → 1끼 분할)
        Map<String, Double> dailyTargets = nutrientTargetCalculator.calculateTargets(healthInfo);
        Map<String, Double> targetPerMeal = divideTarget(dailyTargets, 3);

        // 5. 일주일 식단 구성
        Map<String, DailyMeals> weeklyMeals = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            String dateStr = date.toString(); // yyyy-MM-dd

            List<Food> mealFoods = generateOneMeal(allFoods, targetPerMeal, allergies, dislikes, recentFoods);

            // 한 끼: 밥, 국, 반찬3 → DailyMeals에 아침/점심/저녁 모두 동일하게 구성
            List<String> names = mealFoods.stream().map(Food::getName).toList();

            DailyMeals daily = new DailyMeals();
            daily.setBreakfast(names);
            daily.setLunch(names);
            daily.setDinner(names);

            weeklyMeals.put(dateStr, daily);
        }

        return weeklyMeals;
    }

    
    private Map<String, Double> divideTarget(Map<String, Double> dailyTarget, int mealsPerDay) {
        Map<String, Double> result = new HashMap<>();
        if (dailyTarget == null || mealsPerDay <= 0) return result;

        for (Map.Entry<String, Double> entry : dailyTarget.entrySet()) {
            double divided = entry.getValue() / mealsPerDay;
            result.put(entry.getKey(), divided);
        }

        return result;
    }

    public boolean hasExistingMealPlan(String userId) {
        // MongoDB에서 해당 유저의 meal_recommendation_logs가 있는지 확인
        return recentRecommendationLogService.existsByUserIdAndDate(userId, LocalDate.now());
    }

    public void createAndSaveWeeklyMeal(String userId) {
        Map<String, DailyMeals> meals = recommendMealForUser(userId);

        for (Map.Entry<String, DailyMeals> entry : meals.entrySet()) {
            MealRecommendationLog log = MealRecommendationLog.builder()
                .userId(userId)
                .date(LocalDate.parse(entry.getKey()))
                .breakfast(entry.getValue().getBreakfast())
                .lunch(entry.getValue().getLunch())
                .dinner(entry.getValue().getDinner())
                .build();

            recentRecommendationLogService.save(log);
        }
    }




}
