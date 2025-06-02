package com.capstone.backend.service;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.NutrientStatusMapping;
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

import java.io.File;
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

    public HealthInfo getHealthInfoByUserId(String userId) {
        return healthInfoRepository.findByUserid(userId)
            .orElseThrow(() -> new IllegalArgumentException("건강 정보가 존재하지 않습니다: " + userId));
    }


    public void extractAndSaveDiseasesFromImage(String userId, File imageFile) {
    List<String> diseaseIds = ocrService.extractDiseaseIdsFromImage(imageFile);
    if (diseaseIds == null || diseaseIds.isEmpty()) {
        logger.debug("질병 추론 결과 없음. 저장 생략. userId: {}", userId);
        return;
    }

    HealthInfo info = healthInfoRepository.findByUserid(userId)
            .orElse(HealthInfo.builder().userid(userId).build());
    info.setDiseases(diseaseIds);
    healthInfoRepository.save(info);
    }
}