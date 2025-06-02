package com.capstone.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FoodItemResponse {
    private String name;
    private String imageUrl;
    private List<NutrientIntake> nutrients;
    private List<String> ingredients;
    private String recipe;
}
