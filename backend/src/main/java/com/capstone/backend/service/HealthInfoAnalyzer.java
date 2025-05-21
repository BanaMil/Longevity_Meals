package com.capstone.backend.service;

import com.capstone.backend.domain.Allergy;
import com.capstone.backend.domain.DiseaseKeywordMapping;
import com.capstone.backend.domain.DiseaseNutrientRelation;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.AllergyRepository;
import com.capstone.backend.repository.DiseaseKeywordRepository;
import com.capstone.backend.repository.DiseaseNutrientRelationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthInfoAnalyzer {

    private final DiseaseKeywordRepository keywordRepo;
    private final DiseaseNutrientRelationRepository nutrientRepo;
    private final AllergyRepository allergyRepo;

    public HealthInfoRequest analyze(String ocrText) {
        log.info("Analyzing OCR text: {}", ocrText);

        // 1. 질병 유추
        List<String> diseases = inferDiseasesFromKeywords(ocrText);

        // 2. 알레르기 추출
        List<String> allergies = inferAllergiesFromKeywords(ocrText);

        // 3. 질병별 영양소 추출
        List<String> recommended = new ArrayList<>();
        List<String> restricted = new ArrayList<>();

        for (String disease : diseases) {
            for (DiseaseNutrientRelation relation : nutrientRepo.findByDisease(disease)) {
                switch (relation.getRelation()) {
                    case "recommended" -> recommended.add(relation.getNutrient());
                    case "restricted" -> restricted.add(relation.getNutrient());
                }
            }
        }

        return HealthInfoRequest.builder()
                .diseases(diseases)
                .allergies(allergies)
                .recommendedNutrients(recommended)
                .restrictedNutrients(restricted)
                .build();
    }

    private List<String> inferDiseasesFromKeywords(String text) {
        return keywordRepo.findAll().stream()
                .filter(mapping -> mapping.getKeywords().stream().anyMatch(text::contains))
                .map(DiseaseKeywordMapping::getDiseaseName)
                .distinct()
                .toList();
    }

    private List<String> inferAllergiesFromKeywords(String text) {
        List<String> results = new ArrayList<>();
        for (Allergy allergy : allergyRepo.findAll()) {
            for (String keyword : allergy.getIngredientKeywords()) {
                if (text.contains(keyword)) {
                    results.add(allergy.getName());
                    break; // 해당 알러지는 한 번 매칭되면 추가
                }
            }
        }
        return results.stream().distinct().toList();
    }
}
