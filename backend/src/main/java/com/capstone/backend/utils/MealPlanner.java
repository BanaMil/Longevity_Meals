package com.capstone.backend.utils;

import com.capstone.backend.domain.Food;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class MealPlanner {

    public static double computeContribution(Food food, Map<String, Double> current, Map<String, Double> target) {
        double score = 0;
        for (String nutrient : target.keySet()) {
            double value = food.getNutrients().getOrDefault(nutrient, 0.0);
            double expected = current.getOrDefault(nutrient, 0.0) + value;
            double targetVal = target.get(nutrient);
            if (targetVal != 0) {
                double contribution = 1 - Math.abs(expected - targetVal) / targetVal;
                score += contribution;
            }
        }
        return score;
    }

    public static List<Food> chooseMeal(List<Food> candidates, Map<String, Double> current, Map<String, Double> target, int mealSize, int topK) {
        List<FoodScore> scored = candidates.stream()
            .map(f -> new FoodScore(f, computeContribution(f, current, target)))
            .sorted(Comparator.comparingDouble(FoodScore::getScore).reversed())
            .limit(topK)
            .collect(Collectors.toList());

        Random rand = new Random();
        List<Food> chosen = new ArrayList<>();
        Map<String, Double> updated = new HashMap<>(current);

        for (int i = 0; i < mealSize && !scored.isEmpty(); i++) {
            Food selected = scored.get(rand.nextInt(scored.size())).getFood();
            chosen.add(selected);

            for (String nutrient : target.keySet()) {
                double value = selected.getNutrients().getOrDefault(nutrient, 0.0);
                updated.put(nutrient, updated.getOrDefault(nutrient, 0.0) + value);
            }

            scored.removeIf(f -> f.getFood().equals(selected)); // 중복 방지
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
