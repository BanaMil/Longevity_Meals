package com.capstone.backend.dto;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodayMealResponse {
    private FoodItemResponse rice;
    private FoodItemResponse soup;
    private List<FoodItemResponse> sideDishes;
}