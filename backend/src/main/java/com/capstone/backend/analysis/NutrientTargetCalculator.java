package com.capstone.backend.analysis;

import com.capstone.backend.repository.NutrientReferenceRepository;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.NutrientReference;

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

    public Map<String, Double> calculateTargets(List<NutrientStatusMapping> statusList) {
        Map<String, Double> result = new HashMap<>();

        for (NutrientStatusMapping status : statusList) {
            String nutrient = status.getNutrient();
            double modifier = status.getModifier();

            Optional<NutrientReference> optionalRef = referenceRepo.findByNutrient(nutrient);
            if (optionalRef.isEmpty()) continue;

            NutrientReference ref = optionalRef.get();
            Double baseAmount = ref.getRecommendedAmount();
            Double upperLimit = ref.getUpperLimit();

            if (baseAmount == null) continue;

            double personalized = baseAmount * modifier;
            if (upperLimit != null && personalized > upperLimit) {
                personalized = upperLimit;
            }

            result.put(nutrient, personalized);
        }

        return result;
    }
}
