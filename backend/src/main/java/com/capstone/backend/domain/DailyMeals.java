package com.capstone.backend.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DailyMeals {
    private String Date;
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
}
