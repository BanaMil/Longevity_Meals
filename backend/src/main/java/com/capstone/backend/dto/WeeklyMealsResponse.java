package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WeeklyMealsResponse {
    private Map<String, DailyMealsResponse> meals; // key: "yyyy-MM-dd"
}
