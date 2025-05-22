package com.capstone.backend.domain;

import com.capstone.backend.domain.enums.NutrientRelation;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class NutrientStatusMapping {
    private String nutrient;
    private NutrientRelation status;
    private double weight;
    private double modifier;
}
