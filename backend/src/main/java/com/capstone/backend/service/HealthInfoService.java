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
        logger.info("[건강정보 저장] userId: {}", userId);
        
        // 기존 건강정보 조회
        HealthInfo existingHealthInfo = null;
        try {
            existingHealthInfo = getHealthInfoByUserId(userId);
            logger.info("[기존 건강정보 발견] userId: {}, 기존 질병 개수: {}", userId, existingHealthInfo.getDiseases().size());
        } catch (Exception e) {
            logger.info("[새 건강정보 생성 예정] userId: {}, 기존 정보 없음", userId);
        }
        
        // 영양소 분석 및 개인화 섭취량 계산
        List<NutrientStatusMapping> statusList = analyzer.analyze(request.getDiseases());
        List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, request.getGender());
        
        HealthInfo healthInfo;
        
        if (existingHealthInfo != null) {
            // 기존 정보 업데이트
            logger.info("[기존 건강정보 업데이트] userId: {}", userId);
            healthInfo = existingHealthInfo;
            
            // 기존 질병과 새 질병 병합 (중복 제거)
            List<String> mergedDiseases = new ArrayList<>(existingHealthInfo.getDiseases());
            for (String disease : request.getDiseases()) {
                if (!mergedDiseases.contains(disease)) {
                    mergedDiseases.add(disease);
                    logger.info("[질병 추가] {}", disease);
                }
            }
            
            // 정보 업데이트
            healthInfo.setGender(request.getGender());
            healthInfo.setHeight(request.getHeight());
            healthInfo.setWeight(request.getWeight());
            healthInfo.setDiseases(mergedDiseases);
            healthInfo.setAllergies(request.getAllergies());
            healthInfo.setDislikes(request.getDislikes());
            healthInfo.setStatusList(statusList);
            healthInfo.setPersonalizedIntake(personalizedIntake);
            
            logger.info("[건강정보 업데이트 완료] 최종 질병 목록: {}", mergedDiseases);
        } else {
            // 새 건강정보 생성
            logger.info("[새 건강정보 생성] userId: {}", userId);
            healthInfo = HealthInfo.builder()
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
        }
        
        // 사용자 상태 업데이트
        User user = userRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.setHealthInfoSubmitted(true);
        
        // 저장
        healthInfoRepository.save(healthInfo);
        userRepository.save(user);
        
        logger.info("[건강정보 저장 완료] userId: {}", userId);
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

    public HealthInfo extractAndSaveCompleteHealthInfo(String userId, File imageFile, 
                                                  Double height, Double weight, String gender,
                                                  List<String> allergies, List<String> dislikes) throws IOException {
        logger.info("[완전한 건강정보 저장] userId: {}, 파일: {}", userId, imageFile.getName());
        logger.info("[입력된 기본정보] 키: {}, 몸무게: {}, 성별: {}, 알레르기: {}, 비선호: {}", 
                    height, weight, gender, allergies, dislikes);
        
        try {
            // 1. Google Document AI로 텍스트 추출
            String extractedText = googleDocumentService.extractTextFromImage(imageFile);
            logger.info("[텍스트 추출 완료] 추출된 텍스트 길이: {}", extractedText.length());
            
            // 2. 추출된 텍스트에서 질병명 파싱
            List<String> diseases = googleDocumentService.extractDiseases(extractedText);
            logger.info("[질병 추출 완료] 발견된 질병: {}", diseases);
            
            // 3. 기존 건강정보 조회
            logger.info("[건강정보 조회 시작] userId: {}", userId);
            HealthInfo healthInfo = null;
            try {
                healthInfo = getHealthInfoByUserId(userId);
                logger.info("[기존 건강정보 발견] userId: {}", userId);
            } catch (Exception e) {
                logger.info("[새 건강정보 생성 예정] userId: {}, 기존 정보 없음", userId);
            }
            
            // 4. 건강정보 업데이트 또는 생성
            if (healthInfo != null) {
                // 기존 정보 업데이트
                logger.info("[기존 건강정보 업데이트] userId: {}", userId);
                
                // 질병 정보 병합
                List<String> existingDiseases = new ArrayList<>(healthInfo.getDiseases());
                for (String disease : diseases) {
                    if (!existingDiseases.contains(disease)) {
                        existingDiseases.add(disease);
                        logger.info("[질병 추가] {}", disease);
                    }
                }
                healthInfo.setDiseases(existingDiseases);
                
                // 기본 정보 업데이트 (입력된 경우만)
                if (height != null) healthInfo.setHeight(height);
                if (weight != null) healthInfo.setWeight(weight);
                if (gender != null && !gender.trim().isEmpty()) healthInfo.setGender(gender);
                if (allergies != null && !allergies.isEmpty()) healthInfo.setAllergies(allergies);
                if (dislikes != null && !dislikes.isEmpty()) healthInfo.setDislikes(dislikes);
                
                // 영양소 분석 및 개인화 섭취량 재계산
                List<NutrientStatusMapping> statusList = analyzer.analyze(healthInfo.getDiseases());
                List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, healthInfo.getGender());
                healthInfo.setStatusList(statusList);
                healthInfo.setPersonalizedIntake(personalizedIntake);
                
            } else {
                // 새 건강정보 생성
                logger.info("[새 건강정보 생성] userId: {}", userId);
                
                // 영양소 분석 및 개인화 섭취량 계산
                List<NutrientStatusMapping> statusList = analyzer.analyze(diseases);
                List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, gender);
                
                healthInfo = HealthInfo.builder()
                        .userid(userId)
                        .gender(gender)
                        .height(height)
                        .weight(weight)
                        .diseases(diseases)
                        .allergies(allergies != null ? allergies : new ArrayList<>())
                        .dislikes(dislikes != null ? dislikes : new ArrayList<>())
                        .statusList(statusList)
                        .personalizedIntake(personalizedIntake)
                        .build();
            }
            
            // 5. 건강정보 저장
            logger.info("[건강정보 저장 시작] userId: {}", userId);
            HealthInfo savedHealthInfo = healthInfoRepository.save(healthInfo);
            logger.info("[건강정보 저장 완료] userId: {}", userId);
            
            // 6. 사용자 상태 업데이트
            User user = userRepository.findByUserid(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
            user.setHealthInfoSubmitted(true);
            userRepository.save(user);
            logger.info("[사용자 상태 업데이트 완료] userId: {}", userId);
            
            logger.info("[완전한 건강정보 저장 완료] userId: {}, 최종 질병: {}", userId, savedHealthInfo.getDiseases());
            return savedHealthInfo;
            
        } catch (Exception e) {
            logger.error("[완전한 건강정보 저장 실패] userId: {}, 오류: {}", userId, e.getMessage());
            logger.error("[스택 트레이스]", e);
            throw e;
        }
    }
}