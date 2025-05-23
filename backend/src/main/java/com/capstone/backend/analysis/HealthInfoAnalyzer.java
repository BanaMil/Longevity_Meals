package com.capstone.backend.analysis;

import com.capstone.backend.domain.DiseaseNutrientRelation;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.enums.NutrientRelation;
import com.capstone.backend.repository.DiseaseNutrientRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HealthInfoAnalyzer {

    private final DiseaseNutrientRelationRepository relationRepo;

    public List<NutrientStatusMapping> analyze(List<String> diseases) {
        Map<String, List<DiseaseNutrientRelation>> relationMap = new HashMap<>();

        // 1. 질병별로 관계 리스트 수집
        for (String disease : diseases) {
            List<DiseaseNutrientRelation> relations = relationRepo.findByDisease(disease);
            for (DiseaseNutrientRelation rel : relations) {
                relationMap.computeIfAbsent(rel.getNutrient(), k -> new ArrayList<>()).add(rel);
            }
        }

        // 2. 영양소별 relation 및 modifier 병합
        List<NutrientStatusMapping> result = new ArrayList<>();
        for (Map.Entry<String, List<DiseaseNutrientRelation>> entry : relationMap.entrySet()) {
            String nutrient = entry.getKey();
            List<DiseaseNutrientRelation> related = entry.getValue();

            // (a) relation 병합: 가장 높은 우선순위
            NutrientRelation finalRelation = related.stream()
                    .map(DiseaseNutrientRelation::getRelation)
                    .max(Comparator.comparingInt(this::priorityOf))
                    .orElse(NutrientRelation.NEUTRAL);

            // (b) modifier 병합: Multiplicative
            double finalModifier = related.stream()
                    .map(DiseaseNutrientRelation::getModifier)
                    .filter(Objects::nonNull)
                    .reduce(1.0, (a, b) -> a * b);

            // (c) weight: 가장 높은 값 사용
            double finalWeight = related.stream()
                    .mapToDouble(DiseaseNutrientRelation::getModifier)
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
