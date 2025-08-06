package com.capstone.backend.domain;

import java.util.List;

import com.capstone.backend.dto.FoodWithIntake;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter @Setter @AllArgsConstructor
public class DailyMeals {
    private String userId;
    private String date;
    private List<FoodWithIntake> breakfast;
    private List<FoodWithIntake> lunch;
    private List<FoodWithIntake> dinner;
}
