package com.capstone.backend.dto;

import com.capstone.backend.domain.NutrientStatusMapping;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.List;

@Getter @Setter @Builder
public class HealthInfoRequest {
    
    private String gender;

    @Positive
    private double height;

    @Positive
    private double weight;

    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;
}
