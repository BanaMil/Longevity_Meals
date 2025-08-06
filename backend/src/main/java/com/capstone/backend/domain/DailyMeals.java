package com.capstone.backend.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter @Setter @AllArgsConstructor
public class DailyMeals {
    private String userId;
    private String date;
    private List<FoodItem> breakfast;
    private List<FoodItem> lunch;
    private List<FoodItem> dinner;
}
