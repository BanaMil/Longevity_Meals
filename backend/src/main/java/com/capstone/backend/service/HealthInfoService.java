package com.capstone.backend.service;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.HealthInfoRepository;
import com.capstone.backend.utils.DiseaseDictionary;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class HealthInfoService {

    private final HealthInfoRepository healthInfoRepository;

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
        String text = extractTextFromImage(imageFile);
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
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata"); // 환경에 맞게 수정
        tesseract.setLanguage("kor");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 텍스트에서 질병명 추출 → 질병 ID 리스트로 변환
    private List<String> extractDiseaseIdsFromText(String text) {
        List<String> diseaseIds = new ArrayList<>();
        for (String word : text.split("[\\s,\\n]+")) {
            if (DiseaseDictionary.contains(word)) {
                String id = DiseaseDictionary.getDiseaseId(word);
                if (!diseaseIds.contains(id)) {
                    diseaseIds.add(id);
                }
            }
        }
        return diseaseIds;
    }
}
