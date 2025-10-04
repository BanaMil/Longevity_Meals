package com.capstone.backend.domain;

import com.capstone.backend.domain.enums.NutrientRelation;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutrientStatusMapping {
    private String nutrient;    // 영양소명
    private NutrientRelation status;
    private Double weight;      // 가중치 (0.0 ~ 1.0)
    private Double modifier;    // 수정자 (배수)

    // 기존 생성자와의 호환성을 위한 생성자 추가
    public NutrientStatusMapping(String nutrient, NutrientRelation status, Double weight) {
        this.nutrient = nutrient;
        this.status = status;
        this.weight = weight;
        this.modifier = 1.0; // 기본값
    }
}
