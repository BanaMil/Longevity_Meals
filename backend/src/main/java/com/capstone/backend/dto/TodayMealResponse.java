package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodayMealResponse {
    private FoodItemResponse rice;
    private FoodItemResponse soup;
    private List<FoodItemResponse> sideDishes;
}