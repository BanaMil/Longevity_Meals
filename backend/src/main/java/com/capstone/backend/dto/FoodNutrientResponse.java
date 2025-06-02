package com.capstone.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class FoodNutrientResponse {
    private String name;
    private String unit;
    private double amount;
}