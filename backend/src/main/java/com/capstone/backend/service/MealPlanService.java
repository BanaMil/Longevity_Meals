package com.capstone.backend.service;

import com.capstone.backend.analysis.NutrientTargetCalculator;
import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.domain.Food;
import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.dto.FoodWithIntake;
import com.capstone.backend.utils.IntakeEstimator;
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
        Set<String> recentFoods,
        Set<String> usedFoodsInDay
    ) {
        // ✅ 1. 명확한 분류 기준 정의
        final Set<String> RICE_CATEGORIES = Set.of("밥류", "죽 및 스프류");
        final Set<String> SOUP_CATEGORIES = Set.of("국 및 탕류", "찌개 및 전골류");
        final Set<String> SIDE_CATEGORIES = Set.of("구이류", "볶음류", "나물, 숙채류", "생채, 무침류", "수, 조, 어, 육류", "장아찌, 절임류", "전, 적 및 부침류", "젓갈류", "조림류", "찜류", "채소, 해조류", "튀김류", "만두류");
        final Set<String> KIMCHI_CATEGORIES = Set.of("김치류");
        final Set<String> ONE_DISH_CATEGORIES = Set.of("국밥류", "면류");

        // ✅ 2. 카테고리 분할
        List<Food> riceList = allFoods.stream()
            .filter(f -> f.getCategory() != null && RICE_CATEGORIES.contains(f.getCategory()))
            .filter(f -> !usedFoodsInDay.contains(f.getName()))
            .collect(Collectors.toList());
        log.info("[식단 생성중] 밥 후보 개수: {}", riceList.size());

        List<Food> soupList = allFoods.stream()
            .filter(f -> f.getCategory() != null && SOUP_CATEGORIES.contains(f.getCategory()))
            .filter(f -> !usedFoodsInDay.contains(f.getName()))
            .collect(Collectors.toList());
        log.info("[식단 생성중] 국 후보 개수: {}", soupList.size());

        List<Food> sideList = allFoods.stream()
            .filter(f -> f.getCategory() != null && SIDE_CATEGORIES.contains(f.getCategory()))
            .filter(f -> !usedFoodsInDay.contains(f.getName()))
            .collect(Collectors.toList());
        log.info("[식단 생성중] 반찬 후보 개수: {}", sideList.size());

        List<Food> oneDishList = allFoods.stream()
            .filter(f -> f.getCategory() != null && ONE_DISH_CATEGORIES.contains(f.getCategory()))
            .filter(f -> !usedFoodsInDay.contains(f.getName()))
            .collect(Collectors.toList());
        log.info("[식단 생성중] 국밥 및 면류 후보 개수: {}", oneDishList.size());

        // 3. 현재 영양소 누적량
        Map<String, Double> current = new HashMap<>();
        Random rand = new Random();
        boolean useOneDish = false;
        

        // 4. 밥 + 국일지 일체형일지 선택 (기여도 + 확률 기반)
        if (!oneDishList.isEmpty() && !riceList.isEmpty() && !soupList.isEmpty()) {
            Food oneDish = MealPlanner.chooseMeal(oneDishList, current, targetPerMeal, 1, 5, allergies, dislikes, recentFoods).get(0);
            double oneDishScore = MealPlanner.computeContribution(oneDish, current, targetPerMeal);

            List<Food> riceCandidates = MealPlanner.chooseMeal(riceList, current, targetPerMeal, 1, 5, allergies, dislikes, recentFoods);
            List<Food> soupCandidates = MealPlanner.chooseMeal(soupList, current, targetPerMeal, 1, 5, allergies, dislikes, recentFoods);

            if (!riceCandidates.isEmpty() && !soupCandidates.isEmpty()) {
                double comboScore = MealPlanner.computeContribution(riceCandidates.get(0), current, targetPerMeal)
                                    + MealPlanner.computeContribution(soupCandidates.get(0), current, targetPerMeal);
                double threshold = 0.3;  // 30% 확률 허용

                useOneDish = oneDishScore > comboScore || rand.nextDouble() < threshold;
                if (useOneDish) {
                    riceList = List.of(oneDish);  // 밥에 포함시켜 하나로 취급
                    soupList = List.of();         // 국 없음
                } else {
                    riceList = riceCandidates;
                    soupList = soupCandidates;
                }
            }
        }

        // 4. 결정된 밥, 국을 불러오고 추천 반찬 선택 (기여도 + softmax 기반)
        Food rice = riceList.get(0);
        updateCurrentNutrients(current, rice);
        log.info("[식단 생성 결과] 밥(또는 일체형): {}", rice.getName());

        Food soup = soupList.isEmpty() ? null : soupList.get(0);
        if (soup != null) {
            updateCurrentNutrients(current, soup);
            log.info("[식단 생성 결과] 국: {}", soup.getName());
        }

        // ✅ 김치 반찬 확보
        List<Food> kimchiList = allFoods.stream()
            .filter(f -> f.getCategory() != null && KIMCHI_CATEGORIES.contains(f.getCategory()))
            .collect(Collectors.toList());

        // ✅ 김치 1개 선택 (없으면 생략)
        List<Food> selectedKimchi = kimchiList.isEmpty()
            ? new ArrayList<>()
            : MealPlanner.chooseMeal(kimchiList, current, targetPerMeal, 1, 3, allergies, dislikes, recentFoods);

        // ✅ 일반 반찬 후보 중 김치 제외한 것들
        List<Food> sideExcludingKimchi = sideList.stream()
            .filter(f -> f.getCategory() == null || !KIMCHI_CATEGORIES.contains(f.getCategory()))
            .collect(Collectors.toList());

        // ✅ 일반 반찬 2개 선택
        List<Food> selectedOthers = MealPlanner.chooseMeal(sideExcludingKimchi, current, targetPerMeal, 2, 10, allergies, dislikes, recentFoods);

        // ✅ 합쳐서 최종 반찬 구성
        List<Food> sides = new ArrayList<>();
        sides.addAll(selectedKimchi);
        sides.addAll(selectedOthers);

        // ✅ current 갱신 및 로그
        sides.forEach(f -> updateCurrentNutrients(current, f));
        log.info("[식단 생성 결과] 반찬(김치 포함): {}", sides.stream().map(Food::getName).toList());

        // 5. 최종 구성
        List<Food> meal = new ArrayList<>();
        meal.add(rice);
        if (soup != null) meal.add(soup);
        meal.addAll(sides);

        // 6. 오늘 추천한 음식 저장 (단, 김치류, 밥류는 제외)
        String riceCategory = rice.getCategory();
        if (rice != null && (riceCategory == null || !riceCategory.contains("밥"))) {
            usedFoodsInDay.add(rice.getName());
        }

        if (soup != null) {
            usedFoodsInDay.add(soup.getName());
        }

        // 반찬 중 김치류를 제외한 것만 추가
        for (Food side : sides) {
            String cat = side.getCategory();
            if (cat == null || !cat.contains("김치")) {
                usedFoodsInDay.add(side.getName());
            }
        }

        return meal;
    }


    /**
     * 선택된 음식의 영양소 값을 current 누적량에 반영
     */
    private void updateCurrentNutrients(Map<String, Double> current, Food food) {
    if (food == null || food.getNutrients() == null || food.getCategory() == null) return;

    double estimatedIntake = IntakeEstimator.getEstimatedIntake(food.getCategory());
    double baseAmount = food.getBaseAmount();

    // baseAmount가 0이거나 음수일 경우, 안전하게 100g으로 대체
    if (baseAmount <= 0.0) baseAmount = 100.0;

    for (Map.Entry<String, Double> entry : food.getNutrients().entrySet()) {
        String nutrient = entry.getKey();
        Double originalAmount = entry.getValue();

        if (nutrient != null && originalAmount != null) {
            double scaledAmount = (estimatedIntake / baseAmount) * originalAmount;
            current.put(nutrient, current.getOrDefault(nutrient, 0.0) + scaledAmount);
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
            Set<String> usedFoods = new HashSet<>(recentFoods);

            List<Food> breakfastFoods = generateOneMeal(allFoods, targetPerMeal, allergies, dislikes, recentFoods, usedFoods);
            List<Food> lunchFoods = generateOneMeal(allFoods, targetPerMeal, allergies, dislikes, recentFoods, usedFoods);
            List<Food> dinnerFoods = generateOneMeal(allFoods, targetPerMeal, allergies, dislikes, recentFoods, usedFoods);

            List<String> breakfastNames = breakfastFoods.stream().map(Food::getName).toList();
            List<String> lunchNames = lunchFoods.stream().map(Food::getName).toList();
            List<String> dinnerNames = dinnerFoods.stream().map(Food::getName).toList();

            DailyMeals daily = new DailyMeals();
            daily.setBreakfast(breakfastNames);
            daily.setLunch(lunchNames);
            daily.setDinner(dinnerNames);

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
            String date = entry.getKey();
            DailyMeals daily = entry.getValue();

            List<FoodWithIntake> breakfast = convertToFoodWithIntake(daily.getBreakfast());
            List<FoodWithIntake> lunch = convertToFoodWithIntake(daily.getLunch());
            List<FoodWithIntake> dinner = convertToFoodWithIntake(daily.getDinner());

            MealRecommendationLog log = MealRecommendationLog.builder()
                .userId(userId)
                .date(LocalDate.parse(date))
                .breakfast(breakfast)
                .lunch(lunch)
                .dinner(dinner)
                .build();

            recentRecommendationLogService.save(log);
        }
    }

    /**
     * 음식 이름 리스트를 FoodWithIntake 리스트로 변환
     */
    private List<FoodWithIntake> convertToFoodWithIntake(List<String> foodNames) {
        return foodNames.stream()
            .map(name -> {
                Food food = foodService.findByName(name); // 음식 이름으로 Food 객체 조회
                double intake = IntakeEstimator.getEstimatedIntake(food.getCategory());
                return new FoodWithIntake(name, intake);
            })
            .collect(Collectors.toList());
    }

    public Map<String, DailyMeals> loadSavedWeeklyMeals(String userId) {
        List<MealRecommendationLog> logs = recentRecommendationLogService.findLatestWeeklyLogs(userId);

        Map<String, DailyMeals> weeklyMeals = new HashMap<>();
        for (MealRecommendationLog log : logs) {
            DailyMeals daily = new DailyMeals();
            daily.setBreakfast(log.getBreakfast().stream().map(FoodWithIntake::getName).toList());
            daily.setLunch(log.getLunch().stream().map(FoodWithIntake::getName).toList());
            daily.setDinner(log.getDinner().stream().map(FoodWithIntake::getName).toList());
            weeklyMeals.put(log.getDate().toString(), daily);
        }

        return weeklyMeals;
    }

}
