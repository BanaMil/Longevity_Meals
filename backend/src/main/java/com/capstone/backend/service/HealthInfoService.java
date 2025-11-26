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
import com.capstone.backend.ocr.DiseaseExtractor;

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

        // 기존 건강정보 조회 (Optional 방식)
        HealthInfo existingHealthInfo = healthInfoRepository.findByUserid(userId).orElse(null);
        if (existingHealthInfo != null) {
            logger.info("[기존 건강정보 발견] userId: {}, 기존 질병 개수: {}", userId,
                        existingHealthInfo.getDiseases() == null ? 0 : existingHealthInfo.getDiseases().size());
        } else {
            logger.info("[새 건강정보 생성 예정] userId: {}, 기존 정보 없음", userId);
        }

        // 기존 질병 + 직접 입력 질병 통합
        List<String> mergedDiseases = new ArrayList<>();
        if (existingHealthInfo != null && existingHealthInfo.getDiseases() != null) {
            mergedDiseases.addAll(existingHealthInfo.getDiseases());
        }
        if (request.getDiseases() != null) {
            for (String disease : request.getDiseases()) {
                if (!mergedDiseases.contains(disease)) {
                    mergedDiseases.add(disease);
                    logger.info("[질병 추가] {}", disease);
                }
            }
        }

        // 영양소 분석 및 개인화 섭취량 계산 (통합 질병 기준)
        logger.info("[영양소 분석 시작] 통합 질병 목록: {}", mergedDiseases);
        List<NutrientStatusMapping> statusList = analyzer.analyze(mergedDiseases);
        List<PersonalizedIntake> personalizedIntake = nutrientTargetCalculator.calculateTargets(statusList, request.getGender());

        HealthInfo healthInfo;
        if (existingHealthInfo != null) {
            // 기존 정보 업데이트
            logger.info("[기존 건강정보 업데이트] userId: {}", userId);
            healthInfo = existingHealthInfo;
            
            // 기존 질병과 새 질병 병합 (중복 제거)
            mergedDiseases = new ArrayList<>(existingHealthInfo.getDiseases() != null ? existingHealthInfo.getDiseases() : List.of());
            if (request.getDiseases() != null) {
                for (String disease : request.getDiseases()) {
                    if (!mergedDiseases.contains(disease)) {
                        mergedDiseases.add(disease);
                        logger.info("[질병 추가] {}", disease);
                    }
                }
            }
            
            // 정보 업데이트: 요청에 값이 있을 때만 덮어쓰기(없으면 기존값 유지)
            if (request.getGender() != null && !request.getGender().isBlank()) {
                healthInfo.setGender(request.getGender());
            }
            if (request.getHeight() != null && request.getHeight() > 0) {
                healthInfo.setHeight(request.getHeight());
            }
            if (request.getWeight() != null && request.getWeight() > 0) {
                healthInfo.setWeight(request.getWeight());
            }

            // allergies / dislikes : null 요청이면 기존값 유지
            if (request.getAllergies() != null) {
                healthInfo.setAllergies(request.getAllergies());
            } else if (healthInfo.getAllergies() == null) {
                healthInfo.setAllergies(new ArrayList<>());
            }

            if (request.getDislikes() != null) {
                healthInfo.setDislikes(request.getDislikes());
            } else if (healthInfo.getDislikes() == null) {
                healthInfo.setDislikes(new ArrayList<>());
            }

            // 질병은 통합된 mergedDiseases로 설정
            healthInfo.setDiseases(mergedDiseases);
            
            // 통합 질병 기준으로 StatusList / PersonalizedIntake 재계산
            List<NutrientStatusMapping> recalculatedStatus = analyzer.analyze(mergedDiseases);
            List<PersonalizedIntake> recalculatedIntake = nutrientTargetCalculator.calculateTargets(recalculatedStatus, healthInfo.getGender());
            healthInfo.setStatusList(recalculatedStatus);
            healthInfo.setPersonalizedIntake(recalculatedIntake);
            
            logger.info("[건강정보 업데이트 완료] 최종 질병 목록: {}", mergedDiseases);
        } else {
            // 새 건강정보 생성 — 요청에 없는 리스트 필드는 빈 리스트로 초기화
            logger.info("[새 건강정보 생성] userId: {}", userId);
            List<String> diseasesForNew = request.getDiseases() != null ? request.getDiseases() : new ArrayList<>();
            List<NutrientStatusMapping> statusListForNew = analyzer.analyze(diseasesForNew);
            List<PersonalizedIntake> intakeForNew = nutrientTargetCalculator.calculateTargets(statusListForNew, request.getGender());
            
            healthInfo = HealthInfo.builder()
                    .userid(userId)
                    .gender(request.getGender())
                    .height(request.getHeight())
                    .weight(request.getWeight())
                    .diseases(diseasesForNew)
                    .allergies(request.getAllergies() != null ? request.getAllergies() : new ArrayList<>())
                    .dislikes(request.getDislikes() != null ? request.getDislikes() : new ArrayList<>())
                    .statusList(statusListForNew)
                    .personalizedIntake(intakeForNew)
                    .build();
            
            logger.info("[Builder 생성 후 StatusList] {}", healthInfo.getStatusList());
        }

        // 사용자 상태 업데이트 및 저장 (single save)
        User user = userRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.setHealthInfoSubmitted(true);

        HealthInfo savedHealthInfo = healthInfoRepository.save(healthInfo);
        userRepository.save(user);

        logger.info("[저장 완료] userId: {}, healthInfoId: {}", userId, savedHealthInfo.getId());
    }

    public HealthInfo getHealthInfoByUserId(String userId) {
        return healthInfoRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("건강 정보가 존재하지 않습니다: " + userId));
    }


    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) throws IOException {
        logger.info("[건강검진 결과서 분석] userId: {}, 파일: {}", userId, imageFile.getName());

        String text = googleDocumentService.extractTextFromImage(imageFile);
        logger.info("[텍스트 추출 완료] 길이: {}", text == null ? 0 : text.length());

        // ① 질병만 추출
        List<String> extracted = DiseaseExtractor.extractDiseases(text);

        // ② 키/몸무게/성별 추출
        Double heightCm = OcrParsers.extractHeightCm(text);
        Double weightKg = OcrParsers.extractWeightKg(text);
        String genderStd = OcrParsers.extractGenderStd(text);

        // Fetch existing HealthInfo by userid (avoid exception flow)
        HealthInfo hi = healthInfoRepository.findByUserid(userId).orElse(null);
        if (hi == null) {
            hi = new HealthInfo();
            hi.setUserid(userId);
            hi.setAllergies(new ArrayList<>());
            hi.setDislikes(new ArrayList<>());
            hi.setDiseases(new ArrayList<>());
            if (hi.getGender() == null) hi.setGender("male");
            logger.info("[새 HealthInfo 인스턴스 생성] userId: {}", userId);
        } else {
            logger.info("[기존 HealthInfo 로드] userId: {}, diseasesCount: {}", userId,
                        hi.getDiseases() == null ? 0 : hi.getDiseases().size());
        }

        // merge attributes if present (do not overwrite valid existing values unless empty)
        if (heightCm != null && heightCm > 0 && (hi.getHeight() == null || hi.getHeight() <= 0)) {
            hi.setHeight(heightCm);
            logger.info("[사진 기반] 키 설정: {}", heightCm);
        }
        if (weightKg != null && weightKg > 0 && (hi.getWeight() == null || hi.getWeight() <= 0)) {
            hi.setWeight(weightKg);
            logger.info("[사진 기반] 몸무게 설정: {}", weightKg);
        }
        if (genderStd != null && (hi.getGender() == null || hi.getGender().isBlank())) {
            hi.setGender(genderStd);
            logger.info("[사진 기반] 성별 설정: {}", genderStd);
        }

        // ⑤ 질병 병합(중복 제거)
        Set<String> merged = new LinkedHashSet<>();
        if (hi.getDiseases() != null) merged.addAll(hi.getDiseases());
        for (String d : extracted) if (d != null && !d.isBlank()) merged.add(d);
        hi.setDiseases(new ArrayList<>(merged));

        // ⑥ 영양 분석 재계산
        var statusList = analyzer.analyze(hi.getDiseases());
        hi.setStatusList(statusList);
        var targets = nutrientTargetCalculator.calculateTargets(statusList, hi.getGender());
        hi.setPersonalizedIntake(targets);

        // Save single document (update or create)
        HealthInfo saved = healthInfoRepository.save(hi);
        logger.info("[저장 결과] userId={}, healthInfoId={}, diseases={}", userId, saved.getId(), saved.getDiseases());
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