package com.capstone.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "disease_nutrient_relations")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DiseaseNutrientRelation {
    @Id
    private String id;
    private String disease;
    private String nutrient;
    private String relation;  // "recommended" or "restricted"
    private Double weight;    // optional
}