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

        // aggregateNutrientStatuses 호출하여 실제 분석 수행
        List<NutrientStatusMapping> statusList = aggregateNutrientStatuses(diseases);
        
        log.info("[HealthInfoAnalyzer] 분석 완료 - StatusList 개수: {}", statusList.size());
        for (NutrientStatusMapping status : statusList) {
            log.info("[HealthInfoAnalyzer] 매핑 결과: {} -> {}, 가중치: {}, 수정자: {}", 
                    status.getNutrient(), status.getStatus(), status.getWeight(), status.getModifier());
        }
        
        return statusList;
    }

    public List<NutrientStatusMapping> aggregateNutrientStatuses(List<String> diseases) {
        if (diseases == null || diseases.isEmpty()) {
            log.warn("[HealthInfoAnalyzer] 질병 목록이 비어있음");
            return new ArrayList<>();
        }

        Map<String, List<DiseaseNutrientRelation>> groupedByNutrient = relationRepo.findByDisease(diseases)
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

            // (c) weight 계산 (기본값 0.5)
            double finalWeight = 0.5;

            // NutrientRelation enum을 직접 사용
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