package com.capstone.backend.analysis.mergepolicy;

import com.capstone.backend.domain.DiseaseNutrientRelation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component("multiplicativeMergePolicy")
public class MultiplicativeMergePolicy implements ModifierMergePolicy {

    @Override
    public double merge(List<DiseaseNutrientRelation> relations) {
        return relations.stream()
                .map(DiseaseNutrientRelation::getModifier)
                .filter(Objects::nonNull)
                .reduce(1.0, (a, b) -> a * b);
    }
}