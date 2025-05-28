package com.capstone.backend.dto;

import lombok.Setter;

import java.util.List;

@Setter
public class MealResponse {
    private FoodItemResponse rice;
    private FoodItemResponse soup;
    private List<FoodItemResponse> sideDishes;
}
