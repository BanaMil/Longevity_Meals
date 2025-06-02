package com.capstone.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FoodItemResponse {
    private String name;
    private String imageUrl;
    private List<NutrientIntake> nutrients;
    private String ingredients;
    private String recipe;
}
