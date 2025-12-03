package com.capstone.backend.controller;

import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.HealthInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
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
        @RequestParam(value = "userId", required = false) String userId,
        @RequestParam(value = "userid", required = false) String userid,
        @RequestParam("image") MultipartFile image,
        HttpServletRequest request) throws IOException {
        
        String finalUserId = userId != null ? userId : userid;
        log.info("[건강검진 결과서 업로드] 요청 파라미터 전체: {}", request.getParameterMap());
        log.info("[건강검진 결과서 업로드] userId: '{}', 파일명: {}", finalUserId, image.getOriginalFilename());
        
        // userId 검증
        if (finalUserId == null || finalUserId.trim().isEmpty()) {
            log.error("[건강검진 결과서 업로드] userId가 null이거나 빈 문자열입니다");
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "userId가 필요합니다", List.of()));
        }
        
        // 임시 파일 생성
        File tempFile = File.createTempFile("healthscan", ".png");
        image.transferTo(tempFile);

        try {
            // Google Document AI로 질병 정보 추출 및 저장
            healthInfoService.extractAndSaveDiseasesFromImage(finalUserId, tempFile);
            
            // 업데이트된 질병 목록 반환
            com.capstone.backend.domain.HealthInfo updatedInfo = healthInfoService.getHealthInfoByUserId(finalUserId);
            List<String> diseases = updatedInfo != null ? updatedInfo.getDiseases() : List.of();
            
            return ResponseEntity.ok(new ApiResponse<>(true, "건강검진 결과서 분석 완료", diseases));
        } catch (Exception e) {
            log.error("[건강검진 결과서 분석 실패] userId: {}, 오류: {}", finalUserId, e.getMessage());
            // 기본 질병 목록 반환 (분석 실패 시)
            try {
                com.capstone.backend.domain.HealthInfo existingInfo = healthInfoService.getHealthInfoByUserId(finalUserId);
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

    // Global exception handler for multipart size exceeded
    @ControllerAdvice
    static class GlobalMultipartExceptionHandler {
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiResponse<List<String>>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
            // concise error payload
            ApiResponse<List<String>> resp = new ApiResponse<>(false,
                "파일 크기가 허용 한도를 초과했습니다. 업로드 가능한 최대 크기를 줄이거나 서버 설정을 늘려주세요.",
                List.of());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(resp);
        }
    }
}
