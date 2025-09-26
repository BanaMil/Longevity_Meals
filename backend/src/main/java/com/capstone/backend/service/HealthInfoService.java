package com.capstone.backend.service;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.NutrientStatusMapping;
import com.capstone.backend.domain.PersonalizedIntake;
import com.capstone.backend.domain.User;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.HealthInfoRepository;
import com.capstone.backend.repository.UserRepository;
import com.capstone.backend.analysis.HealthInfoAnalyzer;
import com.capstone.backend.analysis.NutrientTargetCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HealthInfoService {

    private static final Logger logger = LoggerFactory.getLogger(HealthInfoService.class);

    private final HealthInfoRepository healthInfoRepository;
    private final UserRepository userRepository;
    private final HealthInfoAnalyzer analyzer;
    private final NutrientTargetCalculator nutrientTargetCalculator;
    private final OCRService ocrService;

    @Value("${tesseract.datapath}")
    private String tessDataPath;

    @Autowired
    private GoogleDocumentService googleDocumentService;


    public void saveHealthInfo(String userId, HealthInfoRequest request) { // 사용자가 직접 입력한 정보 저장
        List<NutrientStatusMapping> statusList = analyzer.analyze(request.getDiseases());
        List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, request.getGender());

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

    public HealthInfo getHealthInfoByUserId(String userId) {
        return healthInfoRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("건강 정보가 존재하지 않습니다: " + userId));
    }


    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) throws IOException {
        logger.info("[건강검진 결과서 분석] userId: {}, 파일: {}", userId, imageFile.getName());
        
        try {
            // Google Document AI로 텍스트 추출
            String extractedText = googleDocumentService.extractTextFromImage(imageFile);
            logger.info("[텍스트 추출 완료] 추출된 텍스트 길이: {}", extractedText.length());
            
            // 추출된 텍스트에서 질병명 파싱
            List<String> diseases = googleDocumentService.extractDiseases(extractedText);
            logger.info("[질병 추출 완료] 발견된 질병: {}", diseases);
            
            // 기존 건강정보 조회 시도
            logger.info("[건강정보 조회 시작] userId: {}", userId);
            HealthInfo healthInfo = null;
            try {
                healthInfo = getHealthInfoByUserId(userId);
                logger.info("[건강정보 조회 성공] userId: {}, 기존 질병 개수: {}", userId, healthInfo.getDiseases().size());
            } catch (Exception e) {
                logger.warn("[건강정보 조회 실패] userId: {}, 오류: {}", userId, e.getMessage());
            }
            
            if (healthInfo != null) {
                logger.info("[기존 건강정보 업데이트 시작] userId: {}", userId);
                List<String> existingDiseases = new ArrayList<>(healthInfo.getDiseases());
                logger.info("[기존 질병 목록] {}", existingDiseases);
                
                for (String disease : diseases) {
                    if (!existingDiseases.contains(disease)) {
                        existingDiseases.add(disease);
                        logger.info("[질병 추가] {}", disease);
                    } else {
                        logger.info("[질병 중복] {} (이미 존재)", disease);
                    }
                }
                
                logger.info("[질병 목록 설정 시작] 최종 질병 목록: {}", existingDiseases);
                healthInfo.setDiseases(existingDiseases);
                
                logger.info("[건강정보 저장 시작] userId: {}", userId);
                healthInfoRepository.save(healthInfo);
                logger.info("[건강정보 저장 완료] userId: {}, 최종 질병 목록: {}", userId, existingDiseases);
            } else {
                logger.warn("[새 건강정보 생성 시작] userId: {}의 기본 건강정보가 없습니다.", userId);
                HealthInfo newHealthInfo = new HealthInfo();
                newHealthInfo.setUserid(userId);
                newHealthInfo.setDiseases(diseases);
                
                logger.info("[새 건강정보 저장 시작] userId: {}, 질병 목록: {}", userId, diseases);
                healthInfoRepository.save(newHealthInfo);
                logger.info("[새 건강정보 저장 완료] userId: {}", userId);
            }
            
            logger.info("[건강검진 결과서 분석 전체 완료] userId: {}", userId);
            
        } catch (Exception e) {
            logger.error("[건강검진 결과서 분석 실패] userId: {}, 오류 유형: {}, 오류 메시지: {}", 
                        userId, e.getClass().getSimpleName(), e.getMessage());
            logger.error("[스택 트레이스]", e);
            throw e;
        }
    }
}