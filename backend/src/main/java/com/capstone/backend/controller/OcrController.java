package com.capstone.backend.controller;

import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final HealthInfoService healthInfoService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<String>>> uploadImage(
        @RequestParam("userId") String userId,
        @RequestParam("image") MultipartFile image) throws IOException {
        
        log.info("[건강검진 결과서 업로드] userId: {}, 파일명: {}", userId, image.getOriginalFilename());
        
        // 임시 파일 생성
        File tempFile = File.createTempFile("healthscan", ".png");
        image.transferTo(tempFile);

        try {
            // Google Document AI로 질병 정보 추출 및 저장
            healthInfoService.extractAndSaveDiseasesFromImage(userId, tempFile);
            
            // 업데이트된 질병 목록 반환
            com.capstone.backend.domain.HealthInfo updatedInfo = healthInfoService.getHealthInfoByUserId(userId);
            List<String> diseases = updatedInfo != null ? updatedInfo.getDiseases() : List.of();
            
            return ResponseEntity.ok(new ApiResponse<>(true, "건강검진 결과서 분석 완료", diseases));
        } catch (Exception e) {
            log.error("[건강검진 결과서 분석 실패] userId: {}, 오류: {}", userId, e.getMessage());
            // 기본 질병 목록 반환 (분석 실패 시)
            try {
                com.capstone.backend.domain.HealthInfo existingInfo = healthInfoService.getHealthInfoByUserId(userId);
                List<String> diseases = existingInfo != null ? existingInfo.getDiseases() : List.of();
                return ResponseEntity.ok(new ApiResponse<>(false, "건강검진 결과서 분석 실패, 기존 정보 반환", diseases));
            } catch (Exception ex) {
                return ResponseEntity.ok(new ApiResponse<>(false, "건강검진 결과서 분석 실패", List.of()));
            }
        } finally {
            // 임시 파일 삭제
            tempFile.delete();
        }
    }
}
