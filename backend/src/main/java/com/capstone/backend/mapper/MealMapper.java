package com.capstone.backend.mapper;

import com.capstone.backend.dto.FoodItemResponse;
import com.capstone.backend.dto.MealResponse;
import com.capstone.backend.domain.Food;

import java.util.List;
import java.util.stream.Collectors;

public class MealMapper {

    public static FoodItemResponse toResponse(Food food) {
        FoodItemResponse dto = new FoodItemResponse();
        dto.setName(food.getName());
        dto.setImageUrl(food.getImageUrl());
        return dto;
    }

    public static MealResponse groupMeal(List<Food> foods) {
        Food rice = foods.stream()
            .filter(f -> f.getCategory().contains("밥"))
            .findFirst()
            .orElse(null);

        Food soup = foods.stream()
            .filter(f -> f.getCategory().contains("국"))
            .findFirst()
            .orElse(null);

        List<Food> sides = foods.stream()
            .filter(f -> !f.getCategory().contains("밥") && !f.getCategory().contains("국"))
            .limit(3)
            .collect(Collectors.toList());

        MealResponse response = new MealResponse();
        response.setRice(toResponse(rice));
        response.setSoup(toResponse(soup));
        response.setSideDishes(sides.stream()
            .map(MealMapper::toResponse)
            .collect(Collectors.toList()));
        return response;
    }
}
