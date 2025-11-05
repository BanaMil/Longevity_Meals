package com.capstone.backend.controller;

import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthInfoController {

    private final HealthInfoService healthInfoService;

    @PostMapping("/health_info")
    public ResponseEntity<ApiResponse<Object>> submitHealthInfo(@Valid @RequestBody HealthInfoRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[건강정보 제출 요청] userId={}, diseases={}, height={}, weight={}, gender={}", 
                 userId, request.getDiseases(), request.getHeight(), request.getWeight(), request.getGender());
        healthInfoService.saveHealthInfo(userId, request);
        log.info("[건강정보 저장 완료] userId={}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "건강 정보 저장 완료", null));
    }
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<String>>> uploadHealthImage(
        @RequestParam("userId") String userId,
        @RequestParam("image") MultipartFile image) throws IOException {
        
        log.info("[건강검진 결과서 업로드] userId: {}, 파일명: {}", userId, image.getOriginalFilename());
        
        // 임시 파일 생성
        File tempFile = File.createTempFile("healthscan", ".png");
        image.transferTo(tempFile);

        try {
            // Google Document AI로 질병 정보 추출 및 저장 (기존 방식)
            healthInfoService.extractAndSaveDiseasesFromImage(userId, tempFile);
            
            // 업데이트된 질병 목록 반환
            com.capstone.backend.domain.HealthInfo updatedInfo = healthInfoService.getHealthInfoByUserId(userId);
            List<String> diseases = updatedInfo != null ? updatedInfo.getDiseases() : List.of();
            
            return ResponseEntity.ok(new ApiResponse<>(true, "건강검진 결과서 분석 완료", diseases));
        } finally {
            // 임시 파일 삭제
            tempFile.delete();
        }
    }

    // 새로운 엔드포인트: 전체 건강정보를 한 번에 입력
    @PostMapping("/upload-complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadCompleteHealthInfo(
        @RequestParam("userId") String userId,
        @RequestParam("image") MultipartFile image,
        @RequestParam(value = "height", required = false) Double height,
        @RequestParam(value = "weight", required = false) Double weight,
        @RequestParam(value = "gender", required = false) String gender,
        @RequestParam(value = "allergies", required = false) String allergiesStr,
        @RequestParam(value = "dislikes", required = false) String dislikesStr) throws IOException {
        
        log.info("[완전한 건강정보 업로드] userId: {}, 파일명: {}", userId, image.getOriginalFilename());
        log.info("[추가 정보] 키: {}, 몸무게: {}, 성별: {}", height, weight, gender);
        
        // 임시 파일 생성
        File tempFile = File.createTempFile("healthscan", ".png");
        image.transferTo(tempFile);

        try {
            // 알레르기, 비선호 음식 리스트 변환
            List<String> allergies = allergiesStr != null && !allergiesStr.trim().isEmpty() 
                ? Arrays.asList(allergiesStr.split(",")) 
                : new ArrayList<>();
            List<String> dislikes = dislikesStr != null && !dislikesStr.trim().isEmpty() 
                ? Arrays.asList(dislikesStr.split(",")) 
                : new ArrayList<>();

            // Google Document AI로 질병 정보 추출 및 전체 건강정보 저장
            com.capstone.backend.domain.HealthInfo savedHealthInfo = healthInfoService.extractAndSaveCompleteHealthInfo(
                userId, tempFile, height, weight, gender, allergies, dislikes);
            
            Map<String, Object> result = new HashMap<>();
            result.put("diseases", savedHealthInfo.getDiseases());
            result.put("height", savedHealthInfo.getHeight());
            result.put("weight", savedHealthInfo.getWeight());
            result.put("gender", savedHealthInfo.getGender());
            result.put("allergies", savedHealthInfo.getAllergies());
            result.put("dislikes", savedHealthInfo.getDislikes());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "건강검진 결과서 분석 및 건강정보 저장 완료", result));
        } finally {
            // 임시 파일 삭제
            tempFile.delete();
        }
    }
}

