package com.capstone.backend.analysis;

import com.capstone.backend.repository.NutrientReferenceRepository;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.NutrientReference;
import com.capstone.backend.domain.NutrientReference.IntakeStandard;
import com.capstone.backend.domain.HealthInfo;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NutrientTargetCalculator {

    private final NutrientReferenceRepository referenceRepo;

    /**
     * HealthInfo를 받아 개인 맞춤 영양소 목표 섭취량을 계산한다.
     */
    public Map<String, Double> calculateTargets(HealthInfo healthInfo) {
        List<NutrientStatusMapping> statusList = healthInfo.getStatusList();
        String gender = healthInfo.getGender();
        return calculateTargets(statusList, gender); // 내부 위임
    }

    /**
     * 질병 기반 영양소 중요도 리스트 + 성별 정보를 기반으로 맞춤 섭취량 계산
     */
    public Map<String, Double> calculateTargets(List<NutrientStatusMapping> statusList, String gender) {
        Map<String, Double> result = new HashMap<>();

        if (!"male".equals(gender) && !"female".equals(gender)) {
            gender = "male";
        }

        // 에너지 먼저 조회 (kcal 기준 섭취량 계산에 필요)
        Optional<NutrientReference> energyRefOpt = referenceRepo.findByNutrient("에너지");
        final String effectiveGender = gender;

        double energyKcal = energyRefOpt.map(ref -> {
            IntakeStandard standard = "male".equals(effectiveGender) ? ref.getMale() : ref.getFemale();
            return (standard != null && standard.getRecommendedAmount() != null)
                ? standard.getRecommendedAmount() : 0;
        }).orElse(0.0);

        for (NutrientStatusMapping status : statusList) {
            String nutrient = status.getNutrient();
            double modifier = status.getModifier();

            Optional<NutrientReference> optionalRef = referenceRepo.findByNutrient(nutrient);
            if (optionalRef.isEmpty()) continue;

            NutrientReference ref = optionalRef.get();
            IntakeStandard standard = "male".equals(gender) ? ref.getMale() : ref.getFemale();

            Double baseAmount = (standard != null) ? standard.getRecommendedAmount() : null;
            Double upperLimit = (standard != null) ? standard.getUpperLimit() : null;

            double personalized;

            if (baseAmount != null) {
                personalized = baseAmount * modifier;
            } else if (
                ref.getKcalPerUnit() != null &&
                standard != null &&
                standard.getMinRatio() != null &&
                standard.getMaxRatio() != null &&
                energyKcal > 0
            ) {
                double minKcal = energyKcal * standard.getMinRatio();
                double maxKcal = energyKcal * standard.getMaxRatio();
                double targetKcal = ((minKcal + maxKcal) / 2.0) * modifier;
                personalized = targetKcal / ref.getKcalPerUnit(); // kcal → 양으로 환산
            } else {
                continue; // 계산 불가
            }

            if (upperLimit != null && personalized > upperLimit) {
                personalized = upperLimit;
            }

            result.put(nutrient, personalized);
        }

        return result;
    }
}
