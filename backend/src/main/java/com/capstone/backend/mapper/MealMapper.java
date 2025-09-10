package com.capstone.backend.mapper;

import com.capstone.backend.dto.FoodItemResponse;
import com.capstone.backend.dto.MealResponse;
import com.capstone.backend.domain.Food;

import java.util.List;
import java.util.stream.Collectors;

public class MealMapper {

    public static FoodItemResponse toResponse(Food food) {
        if (food == null) return null;
        FoodItemResponse dto = new FoodItemResponse();
        dto.setName(food.getName());
        dto.setImageUrl(food.getImageUrl());
        // nutrients 매핑
        if (food.getNutrients() != null) {
            List<com.capstone.backend.dto.NutrientIntake> nutrients = food.getNutrients().entrySet().stream()
                .map(entry -> new com.capstone.backend.dto.NutrientIntake(entry.getKey(), "", entry.getValue()))
                .collect(Collectors.toList());
            dto.setNutrients(nutrients);
        } else {
            dto.setNutrients(null);
        }
        // ingredients 매핑: Food의 List<Ingredient>를 그대로 전달
        dto.setIngredients(food.getIngredients() != null ? food.getIngredients() : List.of());
    // recipe 매핑: Food의 List<String>을 그대로 전달
    dto.setRecipe(food.getRecipe() != null ? food.getRecipe() : List.of());
        return dto;
    }

    public static MealResponse groupMeal(List<Food> foods) {
        Food rice = foods.stream()
            .filter(f -> f.getCategory() != null && f.getCategory().contains("밥"))
            .findFirst()
            .orElse(null);

        Food soup = foods.stream()
            .filter(f -> f.getCategory() != null && f.getCategory().contains("국"))
            .findFirst()
            .orElse(null);

        List<Food> sides = foods.stream()
            .filter(f -> f.getCategory() != null &&
                        !f.getCategory().contains("밥") &&
                        !f.getCategory().contains("국"))
            .limit(3)
            .collect(Collectors.toList());

        MealResponse response = new MealResponse();
        response.setRice(toResponse(rice));
        response.setSoup(toResponse(soup));
        response.setSideDishes(
            sides.stream().map(MealMapper::toResponse).collect(Collectors.toList())
        );
        return response;
    }

}
