package com.capstone.backend.analysis;

import com.capstone.backend.domain.DiseaseNutrientRelation;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.enums.NutrientRelation;
import com.capstone.backend.repository.DiseaseNutrientRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthInfoAnalyzer {

    private final DiseaseNutrientRelationRepository relationRepo;

    public List<NutrientStatusMapping> analyze(List<String> diseases) {
        log.info("[HealthInfoAnalyzer] 분석 시작 - 질병 목록: {}", diseases);

        if (diseases == null || diseases.isEmpty()) {
            log.warn("[HealthInfoAnalyzer] 질병 목록이 비어있음");
            return new ArrayList<>();
        }

        List<NutrientStatusMapping> statusList = new ArrayList<>();

        for (String disease : diseases) {
            log.info("[HealthInfoAnalyzer] 질병 분석 중: {}", disease);
            // 질병별 영양소 매핑 로직
            // 예시: 당뇨병이면 당류 제한, 단백질 권장 등
        }

        log.info("[HealthInfoAnalyzer] 분석 완료 - StatusList 개수: {}", statusList.size());
        return statusList;
    }
    public List<NutrientStatusMapping> aggregateNutrientStatuses(List<String> diseases) {
        if (diseases == null || diseases.isEmpty()) {
            log.warn("[HealthInfoAnalyzer] 질병 목록이 비어있음");
            return new ArrayList<>();
        }

        Map<String, List<DiseaseNutrientRelation>> groupedByNutrient = relationRepo.findByDiseases(diseases)
                .stream()
                .collect(Collectors.groupingBy(DiseaseNutrientRelation::getNutrient));

        List<NutrientStatusMapping> result = new ArrayList<>();

        for (Map.Entry<String, List<DiseaseNutrientRelation>> entry : groupedByNutrient.entrySet()) {
            String nutrient = entry.getKey();
            List<DiseaseNutrientRelation> related = entry.getValue();

            // (a) relation 병합: 가장 높은 우선순위 사용
            NutrientRelation finalRelation = related.stream()
                    .map(DiseaseNutrientRelation::getRelation)
                    .reduce(NutrientRelation::higher)
                    .orElse(NutrientRelation.NEUTRAL);

            // (b) modifier 병합: Multiplicative
            double finalModifier = related.stream()
                    .map(DiseaseNutrientRelation::getModifier)
                    .filter(Objects::nonNull)
                    .reduce(1.0, (a, b) -> a * b);

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
