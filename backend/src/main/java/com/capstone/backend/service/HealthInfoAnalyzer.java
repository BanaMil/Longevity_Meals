package com.capstone.backend.service;

import com.capstone.backend.utils.DiseaseDictionary;
import com.capstone.backend.dto.HealthInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.*;

@Slf4j
@Service
public class HealthInfoAnalyzer {

    private static final Set<String> KNOWN_DISEASES = DiseaseDictionary.getAll().keySet();
    private static final Set<String> KNOWN_ALLERGIES = Set.of("우유", "계란", "땅콩", "대두", "밀", "갑각류");

    public HealthInfoRequest analyze(String ocrText) {
        log.info("Analyzing OCR text: {}", ocrText);

        List<String> diseaseIds = extractDiseaseIds(ocrText);
        List<String> allergies = extractMatches(ocrText, KNOWN_ALLERGIES);

        return HealthInfoRequest.builder()
                .diseases(diseaseIds)  // 여기에 이제 'D001' 같은 ID 저장
                .allergies(allergies)
                .build();
    }

    private List<String> extractDiseaseIds(String text) {
        List<String> ids = new ArrayList<>();
        for (String disease : KNOWN_DISEASES) {
            if (text.contains(disease)) {
                String id = DiseaseDictionary.getDiseaseId(disease);
                if (id != null && !ids.contains(id)) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private List<String> extractMatches(String text, Set<String> keywords) {
        List<String> matches = new ArrayList<>();
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                matches.add(keyword);
            }
        }
        return matches;
    }
}