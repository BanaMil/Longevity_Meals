package com.capstone.backend.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class WeeklyMealsEnvelope {
    private Map<String, DailyMeals> meals;
}
