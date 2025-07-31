package com.capstone.backend.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter @Setter @AllArgsConstructor
public class DailyMeals {
    private String userId;
    private String date;
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
}
