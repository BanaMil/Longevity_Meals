package com.capstone.backend.service;

import com.capstone.backend.domain.DiseaseKeywordMapping;
import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.DiseaseKeywordRepository;
import com.capstone.backend.repository.HealthInfoRepository;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;


@Service
@RequiredArgsConstructor
public class HealthInfoService {

    private static final Logger logger = LoggerFactory.getLogger(HealthInfoService.class);

    private final HealthInfoRepository healthInfoRepository;
    private DiseaseKeywordRepository diseaseKeywordRepository;


    @Value("${tesseract.datapath}")
    private String tessDataPath;


    public void saveHealthInfo(HealthInfoRequest request) { // 사용자가 직접 입력한 정보 저장
        HealthInfo healthInfo = HealthInfo.builder()
                .height(request.getHeight())
                .weight(request.getWeight())
                .diseases(request.getDiseases())
                .allergies(request.getAllergies())
                .dislikes(request.getDislikes())
                .build();

        healthInfoRepository.save(healthInfo);
    }

    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) {

        if (!isValidImageFile(imageFile)) return;
        
        String text = extractTextFromImage(imageFile);  
        if (text == null || text.isBlank()) return;

        List<String> diseaseIds = extractDiseaseIdsFromText(text);

        // 기존 사용자 정보가 있으면 업데이트, 없으면 새로 생성
        HealthInfo info = healthInfoRepository.findById(userId)
                .orElse(HealthInfo.builder().id(userId).build());
        info.setDiseases(diseaseIds);   
        healthInfoRepository.save(info);
    }

    // OCR 텍스트 추출
    private String extractTextFromImage(File imageFile) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath); // 환경에 맞게 수정
        tesseract.setLanguage("kor");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            logger.error("OCR 처리 중 오류 발생. 파일: {}", imageFile.getAbsolutePath(), e);
            return null;
        }
    }

    // 텍스트에서 질병명 추출 → 질병 ID 리스트로 변환
    private List<String> extractDiseaseIdsFromText(String text) {
        Set<String> diseaseIdSet = new HashSet<>();
        Set<String> words = Set.of(text.split("[\\s, \\n]+"));

        for (String word : words) {
            List<DiseaseKeywordMapping> mappings = diseaseKeywordRepository.findByKeywordContaining(word);
            for (DiseaseKeywordMapping mapping : mappings) {
                diseaseIdSet.add(mapping.getDiseaseId());
            }
        }
        return new ArrayList<>(diseaseIdSet);
    }

    private boolean isValidImageFile(File file) {
        if (file == null || !file.exists()) return false;
        if (!file.canRead()) return false;
        if (file.length() == 0 || file.length() > 10 * 1024 * 1024) return false; // 10MB 제한
    
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }
    
}

