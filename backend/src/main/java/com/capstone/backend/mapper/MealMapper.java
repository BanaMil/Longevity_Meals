package com.capstone.backend.mapper;

import com.capstone.backend.dto.MealItemResponse;
import com.capstone.backend.dto.MealResponse;
import com.capstone.backend.domain.Food;

import java.util.stream.Collectors;
import java.util.List;

public class MealMapper {

    public static MealItemResponse toResponse(Food food) {
        MealItemResponse dto = new MealItemResponse();
        dto.setName(food.getName());
        dto.setImageUrl(food.getImageUrl()); // Food 도메인에 imageUrl 필드 필요
        dto.setNutrients(food.getNutrients());
        dto.setNutrients(food.getIngredients());
        dto.setRecipe(food.getRecipe());
        return dto;
    }

    public static MealResponse groupMeal(List<Food> foods) {
        MealResponse res = new MealResponse();
        res.setRice(foods.stream()
            .filter(f -> f.getCategory().contains("밥"))
            .map(MealMapper::toResponse)
            .collect(Collectors.toList()));
        res.setSoup(foods.stream()
            .filter(f -> f.getCategory().contains("국"))
            .map(MealMapper::toResponse)
            .collect(Collectors.toList()));
        res.setSides(foods.stream()
            .filter(f -> !f.getCategory().contains("밥s") && !f.getCategory().contains("국"))
            .map(MealMapper::toResponse)
            .collect(Collectors.toList()));
        return res;
    }
}
