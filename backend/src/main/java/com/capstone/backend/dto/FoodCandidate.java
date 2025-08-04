package com.capstone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodCandidate {
    private String name;
    private List<String> ingredients;
    private Map<String, Double> nutrients;
    private double score;
}
