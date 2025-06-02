package com.capstone.backend.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

import java.util.List;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealResponse {
    private FoodItemResponse rice;
    private FoodItemResponse soup;
    private List<FoodItemResponse> sideDishes;   
}
