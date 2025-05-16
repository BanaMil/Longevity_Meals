package com.capstone.backend.controller;

import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String uploadHealthImage(@RequestParam("userId") String userId,
                                    @RequestParam("image") MultipartFile image) throws IOException {
        File tempFile = File.createTempFile("healthscan", ".png");
        image.transferTo(tempFile);

        healthInfoService.extractAndSaveDiseasesFromImage(userId, tempFile);
        return "질병 정보 저장 완료";
    }
}

