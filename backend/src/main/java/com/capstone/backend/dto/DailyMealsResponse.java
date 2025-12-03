package com.capstone.backend.dto;

import java.util.List;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data @AllArgsConstructor @NoArgsConstructor @Getter
public class DailyMealsResponse {
    private String date;
    private List<FoodWithIntake> breakfast;
    private List<FoodWithIntake> lunch;
    private List<FoodWithIntake> dinner;
}