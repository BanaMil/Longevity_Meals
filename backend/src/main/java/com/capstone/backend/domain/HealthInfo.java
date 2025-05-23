package com.capstone.backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Document(collection = "health_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HealthInfo {
    @Id
    private String id;

    private String userid;
    private String gender;
    private double height;
    private double weight;
    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;
    private List<NutrientStatusMapping> statusList;
    private Map<String, Double> personalizedIntake;
}
