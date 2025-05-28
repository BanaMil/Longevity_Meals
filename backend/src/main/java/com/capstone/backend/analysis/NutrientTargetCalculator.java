package com.capstone.backend.analysis;

import com.capstone.backend.repository.NutrientReferenceRepository;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.NutrientReference;
import com.capstone.backend.domain.NutrientReference.IntakeStandard;

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

    public Map<String, Double> calculateTargets(List<NutrientStatusMapping> statusList, String gender) {
        Map<String, Double> result = new HashMap<>();
        

        // 기본값 처리
        if (!"male".equals(gender) && !"female".equals(gender)) {
            gender = "male";
        }

        // 에너지 먼저 조회 (칼로리 기반 계산용)
        Optional<NutrientReference> energyRefOpt = referenceRepo.findByNutrient("에너지");
        final String effectiveGender = gender;

        double energyKcal = energyRefOpt.map(ref -> {
            IntakeStandard standard = "male".equals(effectiveGender) ? ref.getMale() : ref.getFemale();
            return standard != null && standard.getRecommendedAmount() != null ? standard.getRecommendedAmount() : 0;
        }).orElse(0.0);

        for (NutrientStatusMapping status : statusList) {
            String nutrient = status.getNutrient();
            double modifier = status.getModifier();

            Optional<NutrientReference> optionalRef = referenceRepo.findByNutrient(nutrient);
            if (optionalRef.isEmpty()) continue;

            NutrientReference ref = optionalRef.get();
            IntakeStandard standard = "male".equals(gender) ? ref.getMale() : ref.getFemale();

            Double baseAmount = standard != null ? standard.getRecommendedAmount() : null;
            Double upperLimit = standard != null ? standard.getUpperLimit() : null;

            double personalized;

            if (baseAmount != null) {
                // 일반적인 base × modifier
                personalized = baseAmount * modifier;
            } else if (ref.getKcalPerUnit() != null && standard != null && standard.getMinRatio() != null && standard.getMaxRatio() != null && energyKcal > 0) {
                // kcal 비율 계산
                double minKcal = energyKcal * standard.getMinRatio();
                double maxKcal = energyKcal * standard.getMaxRatio();
                double targetKcal = ((minKcal + maxKcal) / 2.0) * modifier;
                personalized = targetKcal / ref.getKcalPerUnit(); // 양으로 환산
            } else {
                continue; // 계산 불가
            }

            if (upperLimit != null && personalized > upperLimit){
                personalized = upperLimit;
            }

            result.put(nutrient, personalized);
        }

        return result;
    }
}
