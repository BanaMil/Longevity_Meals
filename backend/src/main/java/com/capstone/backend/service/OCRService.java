package com.capstone.backend.service;

import com.capstone.backend.domain.DiseaseKeywordMapping;
import com.capstone.backend.repository.DiseaseKeywordRepository;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OCRService {

    private static final Logger logger = LoggerFactory.getLogger(OCRService.class);

    private final DiseaseKeywordRepository diseaseKeywordRepository;

    @Value("${tesseract.datapath}")
    private String tessDataPath;

    public List<String> extractDiseaseIdsFromImage(File imageFile) {
        if (!isValidImageFile(imageFile)) return Collections.emptyList();

        String text = extractTextFromImage(imageFile);
        if (text == null || text.isBlank()) return Collections.emptyList();

        return extractDiseaseIdsFromText(text);
    }

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
        if (file.length() == 0 || file.length() > 10 * 1024 * 1024) return false;

        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }
}
