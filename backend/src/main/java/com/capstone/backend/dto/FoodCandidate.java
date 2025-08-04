package com.capstone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import com.capstone.backend.domain.Food;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodCandidate {
    private String name;
    private List<String> ingredients;
    private Map<String, Double> nutrients;
    private double score;

    public static FoodCandidate fromFood(Food food) {
        return new FoodCandidate(
            food.getName(),
            food.getIngredients(),
            food.getNutrients(),
            0
        );
    }
}
