package com.capstone.backend.domain;

import com.capstone.backend.domain.enums.NutrientRelation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;


@Document(collection = "disease_nutrient_relations")
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiseaseNutrientRelation {
    @Id
    private String id;
    private String disease;
    private String nutrient;
    private NutrientRelation relation;
    private Double modifier;
}