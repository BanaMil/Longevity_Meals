package com.capstone.backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capstone.backend.dto.FoodWithIntake;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "meal_recommendation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecommendationLog {
    @Id
    private String id;
    private String userId;
    private LocalDate date;             // 추천된 날짜
    private List<FoodWithIntake> breakfast;
    private List<FoodWithIntake> lunch;
    private List<FoodWithIntake> dinner;
}
