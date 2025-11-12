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
import com.capstone.backend.utils.OcrParsers;

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

    @Autowired
    private GoogleDocumentService googleDocumentService;


    public void saveHealthInfo(String userId, HealthInfoRequest request) {
        logger.info("[건강정보 저장] userId: {}", userId);
        logger.info("[입력된 질병 목록] {}", request.getDiseases());

        // 기존 건강정보 조회
        HealthInfo existingHealthInfo = null;
        try {
            existingHealthInfo = getHealthInfoByUserId(userId);
            logger.info("[기존 건강정보 발견] userId: {}, 기존 질병 개수: {}", userId, existingHealthInfo.getDiseases().size());
        } catch (Exception e) {
            logger.info("[새 건강정보 생성 예정] userId: {}, 기존 정보 없음", userId);
        }

        // 기존 질병 + 직접 입력 질병 통합
        List<String> mergedDiseases = new ArrayList<>();
        if (existingHealthInfo != null && existingHealthInfo.getDiseases() != null) {
            mergedDiseases.addAll(existingHealthInfo.getDiseases());
        }
        for (String disease : request.getDiseases()) {
            if (!mergedDiseases.contains(disease)) {
                mergedDiseases.add(disease);
                logger.info("[질병 추가] {}", disease);
            }
        }

        // 영양소 분석 및 개인화 섭취량 계산 (통합 질병 기준)
        logger.info("[영양소 분석 시작] 통합 질병 목록: {}", mergedDiseases);
        List<NutrientStatusMapping> statusList = analyzer.analyze(mergedDiseases);
        logger.info("[영양소 분석 완료] StatusList 개수: {}", statusList != null ? statusList.size() : "null");
        if (statusList != null) {
            for (NutrientStatusMapping status : statusList) {
                logger.info("[StatusMapping] 영양소: {}, 상태: {}, 가중치: {}", 
                           status.getNutrient(), status.getStatus(), status.getWeight());
            }
        }

        logger.info("[개인화 섭취량 계산 시작] StatusList: {}, 성별: {}", statusList, request.getGender());
        List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, request.getGender());
        logger.info("[개인화 섭취량 계산 완료] PersonalizedIntake 개수: {}", personalizedIntake != null ? personalizedIntake.size() : "null");

        HealthInfo healthInfo;

        if (existingHealthInfo != null) {
            // 기존 정보 업데이트
            logger.info("[기존 건강정보 업데이트] userId: {}", userId);
            healthInfo = existingHealthInfo;

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
            logger.info("[Builder로 생성할 StatusList] {}", statusList);

            healthInfo = HealthInfo.builder()
                    .userid(userId)
                    .gender(request.getGender())
                    .height(request.getHeight())
                    .weight(request.getWeight())
                    .diseases(mergedDiseases)
                    .allergies(request.getAllergies())
                    .dislikes(request.getDislikes())
                    .statusList(statusList)
                    .personalizedIntake(personalizedIntake)
                    .build();

            logger.info("[Builder 생성 후 StatusList] {}", healthInfo.getStatusList());
        }

        // 사용자 상태 업데이트
        User user = userRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.setHealthInfoSubmitted(true);

        // 저장 전 최종 확인
        logger.info("[저장 전 최종 StatusList] {}", healthInfo.getStatusList());
        logger.info("[저장 전 최종 PersonalizedIntake] {}", healthInfo.getPersonalizedIntake());

        // 저장
        HealthInfo savedHealthInfo = healthInfoRepository.save(healthInfo);
        logger.info("[저장 후 HealthInfo] {}", savedHealthInfo);
        HealthInfo reloaded = healthInfoRepository.findByUserid(userId).orElse(null);
        logger.info("[MongoDB에서 재조회] {}", reloaded);

        // 저장 후 확인
        logger.info("[저장 후 StatusList] {}", savedHealthInfo.getStatusList());
        logger.info("[저장 후 PersonalizedIntake] {}", savedHealthInfo.getPersonalizedIntake());
        logger.info("[건강정보 저장 완료] userId: {}", userId);
    }

    public HealthInfo getHealthInfoByUserId(String userId) {
        return healthInfoRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("건강 정보가 존재하지 않습니다: " + userId));
    }


    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) throws IOException {
        logger.info("[건강검진 결과서 분석] userId: {}, 파일: {}", userId, imageFile.getName());
        String extractedText = googleDocumentService.extractTextFromImage(imageFile);
        logger.info("[텍스트 추출 완료] 추출된 텍스트 길이: {}", extractedText.length());

           // (A) 수치 파싱
        Double heightCm = OcrParsers.extractHeightCm(extractedText);     // 기대: 170
        Double weightKg = OcrParsers.extractWeightKg(extractedText);     // 기대: 72
        int[] bp        = OcrParsers.extractBloodPressure(extractedText); // 기대: [150,95]
        Double hba1c    = OcrParsers.extractHbA1c(extractedText);        // 기대: 7.8

        // (B) 질병 추출(룰/사전 보강 필요)
        List<String> diseases = googleDocumentService.extractDiseases(extractedText);
        logger.info("[질병 추출] {}", diseases);

        HealthInfo healthInfo = null;
        try {
            healthInfo = getHealthInfoByUserId(userId);
            logger.info("[건강정보 조회 성공] userId: {}, 기존 질병 개수: {}", userId, healthInfo.getDiseases().size());
        } catch (Exception e) {
            logger.warn("[건강정보 조회 실패] userId: {}, 오류: {}", userId, e.getMessage());
            healthInfo = new HealthInfo();
            healthInfo.setUserid(userId);
            healthInfo.setAllergies(new ArrayList<>());
            healthInfo.setDislikes(new ArrayList<>());
            // gender는 사용자 프로필에서 가져오는 것이 바람직
            if (healthInfo.getGender() == null) healthInfo.setGender("male");
        }

        // (D) 키/몸무게 등 업데이트: 값이 있을 때만 덮어쓰기
        if (heightCm != null && heightCm > 0) healthInfo.setHeight(heightCm);
        if (weightKg != null && weightKg > 0) healthInfo.setWeight(weightKg);
        if (bp != null) { healthInfo.setSystolic(bp[0]); healthInfo.setDiastolic(bp[1]); }
        if (hba1c != null) healthInfo.setHba1c(hba1c);

        // (E) 질병 병합(중복 제거, null-safe)
        List<String> merged = new ArrayList<>();
        if (healthInfo.getDiseases() != null) merged.addAll(healthInfo.getDiseases());
        for (String d : diseases) if (d != null && !merged.contains(d)) merged.add(d);
        healthInfo.setDiseases(merged);

        // (F) 영양 분석/개인화 재계산
        List<NutrientStatusMapping> statusList = analyzer.analyze(merged);
        healthInfo.setStatusList(statusList);
        List<PersonalizedIntake> targets =
            nutrientTargetCalculator.calculateTargets(statusList, healthInfo.getGender());
        healthInfo.setPersonalizedIntake(targets);

        logger.info("[저장 전 StatusList] {}", healthInfo.getStatusList());
        logger.info("[저장 전 PersonalizedIntake] {}", healthInfo.getPersonalizedIntake());
        healthInfoRepository.save(healthInfo);

        logger.info("[건강검진 결과서 분석 완료] userId: {}", userId);
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