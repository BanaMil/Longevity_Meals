package com.capstone.backend.service;

import com.capstone.backend.domain.DiseaseKeywordMapping;
import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.User;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.HealthInfoRepository;
import com.capstone.backend.repository.UserRepository;
import com.capstone.backend.repository.DiseaseKeywordRepository;
import com.capstone.backend.analysis.HealthInfoAnalyzer;
import com.capstone.backend.analysis.NutrientTargetCalculator;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HealthInfoService {

    private static final Logger logger = LoggerFactory.getLogger(HealthInfoService.class);

    private final HealthInfoRepository healthInfoRepository;
    private final UserRepository userRepository;
    private final DiseaseKeywordRepository diseaseKeywordRepository;
    private final HealthInfoAnalyzer analyzer;
    private final NutrientTargetCalculator nutrientTargetCalculator;

    @Value("${tesseract.datapath}")
    private String tessDataPath;


    public void saveHealthInfo(String userId, HealthInfoRequest request) { // 사용자가 직접 입력한 정보 저장
        List<NutrientStatusMapping> statusList = analyzer.analyze(request.getDiseases());
        Map<String, Double> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, request.getGender());

        HealthInfo healthInfo = HealthInfo.builder()
                .userid(userId)
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .diseases(request.getDiseases())
                .allergies(request.getAllergies())
                .dislikes(request.getDislikes())
                .statusList(statusList)
                .personalizedIntake(personalizedIntake)
                .build();
        
        /*Optional<User> optionalUser = userRepository.findByUserid(userId);
        if (optionalUser.isEmpty()){
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }

        User user = optionalUser.get();*/
        User user = userRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.setHealthInfoSubmitted(true);
        

        healthInfoRepository.save(healthInfo);
        userRepository.save(user);
    }

    // private String preprocessText(String rawText) { // 텍스트 전처리 함수
    //     String cleaned = rawText.replaceAll("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]", "");
    //     return cleaned.replaceAll("\\s+", " ").trim();
    // }

    // private List<String> extractWords(String text) { // 단어 추출 함수
    //     return List.of(text.split(" "));
    // }



    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) {
        if (!isValidImageFile(imageFile)) return;

        String text = extractTextFromImage(imageFile);
        if (text == null || text.isBlank()) return;

        List<String> diseaseIds = extractDiseaseIdsFromText(text);
        if (diseaseIds == null || diseaseIds.isEmpty()) {
            logger.debug("질병 추론 결과 없음. 저장 생략. userId: {}", userId);
            return;
        }

        HealthInfo info = healthInfoRepository.findByUserid(userId)
                .orElse(HealthInfo.builder().userid(userId).build());
        info.setDiseases(diseaseIds);
        healthInfoRepository.save(info);
    }

    // OCR 텍스트 추출
    private String extractTextFromImage(File imageFile) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("kor");

        try {
            String result = tesseract.doOCR(imageFile);
            logger.debug("OCR 추출 결과: \n{}", result);
            return result;
        } catch (TesseractException e) {
            logger.error("OCR 처리 중 오류 발생. 파일: {}", imageFile.getAbsolutePath(), e);
            return null;
        }
    }


    private List<String> extractDiseaseIdsFromText(String rawText) {
        String cleanedText = preprocessText(rawText);
        List<String> words = extractWords(cleanedText);
        return inferDiseasesFromWords(words);
    }

    private String preprocessText(String rawText) {
        String cleaned = rawText.replaceAll("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]", "");
        return cleaned.replaceAll("\\s+", " ").trim();
    }

    private List<String> extractWords(String text) {
        return Arrays.asList(text.split(" "));
    }

    // 텍스트에서 질병명 추출 → 질병 ID 리스트로 변환
    private List<String> inferDiseasesFromWords(List<String> words) {
        Set<String> diseaseIdSet = new HashSet<>();
        for (String word : words) {
            if (word.isBlank()) continue;
            List<DiseaseKeywordMapping> mappings = diseaseKeywordRepository.findByKeywordContaining(word);
            for (DiseaseKeywordMapping mapping : mappings) {
                diseaseIdSet.add(mapping.getDiseaseId());
            }
        }
        logger.debug("추론된 질병 ID 목록: {}", diseaseIdSet);
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