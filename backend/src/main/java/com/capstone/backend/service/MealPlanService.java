package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.utils.MealPlanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    /**
     * 한 끼 식단을 구성합니다: 밥 1개, 국 1개, 반찬 3개
     *
     * @param allFoods       필터링된 전체 음식 목록
     * @param targetPerMeal  1끼 기준 영양소 목표값
     * @return 구성된 Food 리스트 (1끼)
     */
    public List<Food> generateOneMeal(List<Food> allFoods, Map<String, Double> targetPerMeal) {
        // 1. 카테고리 분할
        List<Food> riceList = allFoods.stream()
            .filter(f -> f.getCategory() != null && f.getCategory().contains("밥"))
            .collect(Collectors.toList());

        List<Food> soupList = allFoods.stream()
            .filter(f -> f.getCategory() != null && f.getCategory().contains("국"))
            .collect(Collectors.toList());

        List<Food> sideList = allFoods.stream()
            .filter(f -> f.getCategory() != null &&
                !f.getCategory().contains("밥") &&
                !f.getCategory().contains("국"))
            .collect(Collectors.toList());

        // 2. 현재 영양소 누적량 (처음엔 0)
        Map<String, Double> current = new HashMap<>();

        // 3. 분류별로 추천 음식 선택
        Food rice = MealPlanner.chooseMeal(riceList, current, targetPerMeal, 1, 5).get(0);
        updateCurrentNutrients(current, rice);

        Food soup = MealPlanner.chooseMeal(soupList, current, targetPerMeal, 1, 5).get(0);
        updateCurrentNutrients(current, soup);

        List<Food> sides = MealPlanner.chooseMeal(sideList, current, targetPerMeal, 3, 10);
        sides.forEach(f -> updateCurrentNutrients(current, f));

        // 4. 최종 구성
        List<Food> meal = new ArrayList<>();
        meal.add(rice);
        meal.add(soup);
        meal.addAll(sides);

        return meal;
    }

    /**
     * 선택된 음식의 영양소를 current 맵에 누적 반영
     */
    private void updateCurrentNutrients(Map<String, Double> current, Food food) {
        for (Map.Entry<String, Double> entry : food.getNutrients().entrySet()) {
            String nutrient = entry.getKey();
            Double amount = entry.getValue();
            current.merge(nutrient, amount, Double::sum);
        }
    }
}
