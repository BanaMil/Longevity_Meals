package com.capstone.backend.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capstone.backend.dto.FoodWithIntake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Document(collection = "dailyMeals")
public class DailyMeals {
    @Id
    private String userId;
    private String date;
    private List<FoodWithIntake> breakfast;
    private List<FoodWithIntake> lunch;
    private List<FoodWithIntake> dinner;
}
