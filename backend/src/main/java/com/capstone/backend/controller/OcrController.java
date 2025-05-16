package com.capstone.backend.controller;

import com.capstone.backend.service.HealthInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final HealthInfoService healthInfoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("userid") String userId,
            @RequestParam("image") MultipartFile image
    ) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 파일이 비어 있습니다.");
        }

        try {
            // MultipartFile → File로 변환
            File tempFile = File.createTempFile("upload_", "_" + image.getOriginalFilename());
            image.transferTo(tempFile);

            // OCR 처리 및 질병 정보 저장
            healthInfoService.extractAndSaveDiseasesFromImage(userId, tempFile);

            // 임시 파일 삭제
            if (tempFile.exists()) tempFile.delete();

            return ResponseEntity.ok("OCR 및 건강 정보 저장 성공");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 처리 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }
}
