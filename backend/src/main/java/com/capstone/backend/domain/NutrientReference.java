package com.capstone.backend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "nutrient_reference")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutrientReference {
    @Id
    private String id;
    private String nutrient;
    private String unit;
    private Double recommendedAmount;
    private Double upperLimit;
    private Double minimumAmount;
}
