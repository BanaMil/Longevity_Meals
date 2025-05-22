package com.capstone.backend.analysis;

import com.capstone.backend.repository.DiseaseNutrientRelationRepository;
import com.capstone.backend.analysis.mergepolicy.ModifierMergePolicy;
import com.capstone.backend.domain.DiseaseNutrientRelation;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.enums.NutrientRelation;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthInfoAnalyzer {

    private final DiseaseNutrientRelationRepository relationRepo;
    private final ModifierMergePolicy mergePolicy; // MultiplicativeMergePolicy가 자동 주입됨

    public List<NutrientStatusMapping> analyze(List<String> diseases) {
        Map<String, List<DiseaseNutrientRelation>> relationMap = new HashMap<>();

        for (String disease : diseases) {
            List<DiseaseNutrientRelation> relations = relationRepo.findByDisease(disease);
            for (DiseaseNutrientRelation rel : relations) {
                relationMap.computeIfAbsent(rel.getNutrient(), k -> new ArrayList<>()).add(rel);
            }
        }

        List<NutrientStatusMapping> result = new ArrayList<>();
        for (Map.Entry<String, List<DiseaseNutrientRelation>> entry : relationMap.entrySet()) {
            String nutrient = entry.getKey();
            List<DiseaseNutrientRelation> related = entry.getValue();

            NutrientRelation finalRelation = related.stream()
                    .map(DiseaseNutrientRelation::getRelation)
                    .max(Comparator.comparingInt(this::priorityOf))
                    .orElse(NutrientRelation.NEUTRAL);

            double finalModifier = mergePolicy.merge(related);

            double finalWeight = related.stream()
                    .mapToDouble(rel -> rel.getModifier() != null ? DiseaseNutrientRelation::getModifier : 0.0)
                    .max()
                    .orElse(1.0);

            result.add(new NutrientStatusMapping(nutrient, finalRelation, finalWeight, finalModifier));
        }

        return result;
    }

    private int priorityOf(NutrientRelation r) {
        return switch (r) {
            case RECOMMENDED -> 1;
            case CAUTION -> 2;
            case RESTRICTED -> 3;
            case NEUTRAL -> 0;
        };
    }
}
