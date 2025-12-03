package com.capstone.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutrientIntake {
    private String name;
    private String unit;
    private double amount;
}
