package com.capstone.backend.dto;

import com.capstone.backend.domain.HealthInfo;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class HealthInfoRequest {
    
    private String gender;

    @Positive
    private double height;

    @Positive
    private double weight;

    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;

    public HealthInfoRequest(HealthInfo info) {
        this.gender = info.getGender();
        this.height = info.getHeight();
        this.weight = info.getWeight();
        this.diseases = info.getDiseases();
        this.allergies = info.getAllergies();
        this.dislikes = info.getDislikes();
    }
}
