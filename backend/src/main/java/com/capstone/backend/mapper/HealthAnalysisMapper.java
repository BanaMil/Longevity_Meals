package com.capstone.backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.NutrientReference;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.enums.NutrientRelation;
import com.capstone.backend.dto.HealthAnalysisResponse;
import com.capstone.backend.dto.NutrientIntake;

public class HealthAnalysisMapper {
    public HealthAnalysisResponse buildHealthAnalysisResponse(HealthInfo info, Map<String, NutrientReference> referenceMap) {
    List<String> recommended = new ArrayList<>();
    List<String> restricted = new ArrayList<>();
    List<String> caution = new ArrayList<>();
    List<NutrientIntake> intakeList = new ArrayList<>();

    for (NutrientStatusMapping status : info.getStatusList()) {
        String nutrient = status.getNutrient();
        NutrientRelation rel = status.getStatus();

        switch (rel) {
            case RECOMMENDED -> recommended.add(nutrient);
            case RESTRICTED -> restricted.add(nutrient);
            case CAUTION -> caution.add(nutrient);
        }

        if (rel != NutrientRelation.CAUTION) {
            NutrientReference ref = referenceMap.get(nutrient);
            if (ref != null && ref.getRecommendedAmount() != null) {
                double amount = ref.getRecommendedAmount() * status.getModifier();
                if (ref.getUpperLimit() != null && amount > ref.getUpperLimit()) {
                    amount = ref.getUpperLimit();
                }

                intakeList.add(new NutrientIntake(nutrient, ref.getUnit(), amount));
            }
        }
    }

    return new HealthAnalysisResponse(
        info.getUserid(),
        recommended,
        restricted,
        caution,
        intakeList
    );
}

}
