package com.capstone.backend.controller;

import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthInfoController {

    private final HealthInfoService healthInfoService;

    @PostMapping("/health_info")
    public ResponseEntity<ApiResponse<Object>> submitHealthInfo(@Valid @RequestBody HealthInfoRequest request) {
        healthInfoService.saveHealthInfo(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "건강 정보 저장 완료", null));
    }
}

