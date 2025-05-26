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

        for (NutrientStatusMapping status : statusList) {
            String nutrient = status.getNutrient();
            double modifier = status.getModifier();

            Optional<NutrientReference> optionalRef = referenceRepo.findByNutrient(nutrient);
            if (optionalRef.isEmpty()) continue;

            NutrientReference ref = optionalRef.get();
            IntakeStandard standard = "male".equals(gender) ? ref.getMale() : ref.getFemale();
            if (standard == null || standard.getRecommendedAmount() == null) continue;

            double baseAmount = standard.getRecommendedAmount();
            double upperLimit = standard.getUpperLimit() != null ? standard.getUpperLimit() : Double.MAX_VALUE;

            double personalized = baseAmount * modifier;
            if (personalized > upperLimit) {
                personalized = upperLimit;
            }

            result.put(nutrient, personalized);
        }

        return result;
    }
}
