package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class MealPlanWeeklyRequest {
    private HealthInfoRequest user;
    private List<FoodCandidate> foods;
}

