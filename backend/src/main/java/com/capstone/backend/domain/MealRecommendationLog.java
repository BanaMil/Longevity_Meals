package com.capstone.backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
}
