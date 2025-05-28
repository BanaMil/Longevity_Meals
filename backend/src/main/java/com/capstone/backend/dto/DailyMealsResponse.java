package com.capstone.backend.dto;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class DailyMealsResponse {
    private String date;
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
}
