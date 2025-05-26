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
import org.springframework.stereotype.Component;

@Component
public class HealthAnalysisMapper {

    public HealthAnalysisResponse buildHealthAnalysisResponse(HealthInfo info, Map<String, NutrientReference> referenceMap) {
        List<String> recommended = new ArrayList<>();
        List<String> restricted = new ArrayList<>();
        List<String> caution = new ArrayList<>();

        for (NutrientStatusMapping status : info.getStatusList()) {
            String nutrient = status.getNutrient();
            NutrientRelation rel = status.getStatus();

            switch (rel) {
                case RECOMMENDED -> recommended.add(nutrient);
                case RESTRICTED -> restricted.add(nutrient);
                case CAUTION -> caution.add(nutrient);
            }
        }

        List<NutrientIntake> intakeList = convertToNutrientIntakeList(info.getPersonalizedIntake(), referenceMap);

        return new HealthAnalysisResponse(
                info.getUserid(),
                recommended,
                restricted,
                caution,
                intakeList
        );
    }

    private List<NutrientIntake> convertToNutrientIntakeList(Map<String, Double> intakeMap, Map<String, NutrientReference> referenceMap) {
        List<NutrientIntake> list = new ArrayList<>();

        for (Map.Entry<String, Double> entry : intakeMap.entrySet()) {
            String nutrient = entry.getKey();
            double amount = entry.getValue();
            String unit = referenceMap.containsKey(nutrient)
                    ? referenceMap.get(nutrient).getUnit()
                    : ""; // 단위 정보 없을 경우 공백 처리

            list.add(new NutrientIntake(nutrient, unit, amount));
        }

        return list;
    }
}
