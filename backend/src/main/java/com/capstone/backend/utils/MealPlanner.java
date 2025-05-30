package com.capstone.backend.utils;

import com.capstone.backend.domain.Food;
import com.capstone.backend.dto.NutrientIntake;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MealPlanner {

    public static double computeContribution(Food food, Map<String, Double> current, Map<String, Double> target) {
        double score = 0;

        Map<String, Double> foodMap = food.getNutrients() != null ? food.getNutrients() : Collections.emptyMap();

        for (String nutrient : target.keySet()) {
            double value = foodMap.getOrDefault(nutrient, 0.0);
            double expected = current.getOrDefault(nutrient, 0.0) + value;
            double targetVal = target.get(nutrient);
            if (targetVal != 0) {
                double contribution = 1 - Math.abs(expected - targetVal) / targetVal;
                score += contribution;
            }
        }

        return score;
    }

    public static List<Food> chooseMeal(
        List<Food> candidates,
        Map<String, Double> current,
        Map<String, Double> target,
        int mealSize,
        int topK,
        Set<String> allergies,
        Set<String> dislikes,
        Set<String> recentFoods
    ) {
        // 1. 필터링
        List<Food> filtered = candidates.stream()
            //.filter(f -> f.getIngredients() != null)
            //.filter(f -> allergies.stream().noneMatch(a -> f.getIngredients().contains(a)))
            //.filter(f -> dislikes.stream().noneMatch(d -> f.getIngredients().contains(d)))
            .filter(f -> !recentFoods.contains(f.getName()))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            log.warn("[chooseMeal] 필터링 결과 없음 → candidates: {}", candidates.size());
            return List.of();
        }

        // 2. 기여도 계산
        List<FoodScore> scored = filtered.stream()
            .map(f -> new FoodScore(f, computeContribution(f, current, target)))
            .sorted(Comparator.comparingDouble(FoodScore::getScore).reversed())
            .limit(topK)
            .collect(Collectors.toList());

        if (scored.isEmpty()) {
            log.warn("[chooseMeal] 기여도 점수 없음 → 필터링 후: {}", filtered.size());
            return List.of();
        }
        // 3. Softmax 확률 기반으로 mealSize개 추출
        List<Food> chosen = new ArrayList<>();
        Map<String, Double> updated = new HashMap<>(current);

        Random rand = new Random();

        for (int i = 0; i < mealSize && !scored.isEmpty(); i++) {
            // 소프트맥스 확률 계산
            double temperature = 1.0;  // 값이 작을수록 탐욕적, 클수록 랜덤성 증가
            double[] expScores = scored.stream()
                .mapToDouble(fs -> Math.exp(fs.getScore() / temperature))
                .toArray();

            double sumExp = Arrays.stream(expScores).sum();
            double r = rand.nextDouble() * sumExp;

            double cumulative = 0;
            int selectedIndex = 0;
            for (int j = 0; j < expScores.length; j++) {
                cumulative += expScores[j];
                if (r <= cumulative) {
                    selectedIndex = j;
                    break;
                }
            }

            Food selected = scored.get(selectedIndex).getFood();
            chosen.add(selected);

            // 누적 영양소 갱신
            if (selected.getNutrients() != null) {
                for (Map.Entry<String, Double> entry : selected.getNutrients().entrySet()) {
                    String nutrient = entry.getKey();
                    double value = entry.getValue();
                    updated.put(nutrient, updated.getOrDefault(nutrient, 0.0) + value);
                }
            }

            // 해당 음식 제거
            scored.removeIf(fs -> fs.getFood().equals(selected));
        }

        return chosen;
    }

    @Data
    @AllArgsConstructor
    public static class FoodScore {
        private Food food;
        private double score;
    }
}
