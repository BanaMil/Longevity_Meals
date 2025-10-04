package com.capstone.backend.controller;

import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}

