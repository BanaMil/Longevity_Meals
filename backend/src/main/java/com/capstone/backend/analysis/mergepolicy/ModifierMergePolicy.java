package com.capstone.backend.analysis.mergepolicy;

import com.capstone.backend.domain.DiseaseNutrientRelation;
import java.util.List;

public interface ModifierMergePolicy {
    double merge(List<DiseaseNutrientRelation> relations);
}