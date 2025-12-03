package com.capstone.backend.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAnalysisResponse {
    private String userid;
    private List<String> recommendedNutrients;
    private List<String> restrictedNutrients;
    private List<String> cautionNutrients;
    private List<NutrientIntake> personalizedIntake;
}

