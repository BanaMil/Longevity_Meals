package com.capstone.backend.analysis.mergepolicy;

import com.capstone.backend.domain.DiseaseNutrientRelation;
// import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

// @Component("minMergePolicy")
public class MinMergePolicy implements ModifierMergePolicy {

    @Override
    public double merge(List<DiseaseNutrientRelation> relations) {
        return relations.stream()
                .map(DiseaseNutrientRelation::getModifier)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(1.0);
    }
}