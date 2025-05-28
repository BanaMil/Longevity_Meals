package com.capstone.backend.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DailyMealsResponse {
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
}
