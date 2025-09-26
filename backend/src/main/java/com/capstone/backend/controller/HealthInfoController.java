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
        healthInfoService.saveHealthInfo(userId, request);
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
}

