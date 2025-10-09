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
        
        // log.info("[HealthInfoAnalyzer] 분석 완료 - StatusList 개수: {}", statusList.size());
        // for (NutrientStatusMapping status : statusList) {
        //     log.info("[HealthInfoAnalyzer] 매핑 결과: {} -> {}, 가중치: {}, 수정자: {}", 
        //             status.getNutrient(), status.getStatus(), status.getWeight(), status.getModifier());
        
        
        return statusList;
        }

    public List<NutrientStatusMapping> aggregateNutrientStatuses(List<String> diseases) {
        if (diseases == null || diseases.isEmpty()) {
            log.warn("[HealthInfoAnalyzer] 질병 목록이 비어있음");
            return new ArrayList<>();
        }

        List<DiseaseNutrientRelation> allRelations = new ArrayList<>();
        for (String disease : diseases) {
            String normalized = normalizeDiseaseName(disease);
            List<DiseaseNutrientRelation> diseaseRelations = relationRepo.findByDisease(normalized);
            allRelations.addAll(diseaseRelations);
            log.info("[HealthInfoAnalyzer] 질병 '{}'({}) 관련 영양소 관계 개수: {}", disease, normalized, diseaseRelations.size());
        }

        // 영양소별로 그룹화
        Map<String, List<DiseaseNutrientRelation>> groupedByNutrient = allRelations.stream()
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
            log.info("[HealthInfoAnalyzer] 영양소 '{}' 최종 관계: {}, 가중치: {}, 수정자: {}", 
                    nutrient, finalRelation, finalWeight, finalModifier);
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

    private String normalizeDiseaseName(String disease) {
        // 예시: DB에 저장된 질병명과 매핑
        return switch (disease.trim()) {
            case "당뇨", "당뇨병" -> "당뇨병";
            case "고혈압", "고혈압증" -> "고혈압";
            case "고지혈증" -> "고지혈증";
            case "빈혈" -> "빈혈";
            case "골다공증" -> "골다공증";
            // 필요시 추가
            default -> disease.trim();
        };
    }
}