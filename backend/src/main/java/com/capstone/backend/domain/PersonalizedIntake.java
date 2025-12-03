package com.capstone.backend.domain;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalizedIntake {
    private String nutrient;
    private double amount;
}
