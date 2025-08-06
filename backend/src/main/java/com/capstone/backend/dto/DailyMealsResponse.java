package com.capstone.backend.dto;

import java.util.List;

import com.capstone.backend.domain.FoodItem;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data @AllArgsConstructor @NoArgsConstructor @Getter
public class DailyMealsResponse {
    private String date;
    private List<FoodItem> breakfast;
    private List<FoodItem> lunch;
    private List<FoodItem> dinner;
}