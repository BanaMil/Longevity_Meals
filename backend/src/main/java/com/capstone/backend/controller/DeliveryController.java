package com.capstone.backend.controller;

import com.capstone.backend.domain.enums.DeliveryStatus;
import com.capstone.backend.dto.DeliveryRequest;
import com.capstone.backend.service.DeliveryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

	@Autowired
	private DeliveryService deliveryService;

	// 배송 신청
	@PostMapping("/requests")
	public ResponseEntity<Map<String, Object>> requestDelivery(@RequestBody @Valid DeliveryRequest body) {
        log.info("=== [배송 요청] userId: {}", body.getUserid());
		deliveryService.applySelections(body);
        return ResponseEntity.ok(Map.of("message", "배송 신청 반영 완료")); // 200
    }

	// // 배송 상태 조회
	// @GetMapping("/status")
	// public DeliveryStatus getDeliveryStatus(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	// 	return deliveryService.getDeliveryStatus(userId, date);
	// }

	// // 배송 상태 변경 (관리자/시스템용)
	// @PostMapping("/update-status")
	// public String updateDeliveryStatus(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam DeliveryStatus status) {
	// 	boolean result = deliveryService.updateDeliveryStatus(userId, date, status);
	// 	return result ? "배송 상태 변경 완료" : "해당 식단 기록이 없습니다.";
	// }

	// 요청 바디 DTO
    public static class BulkDeliveryRequest {
        @NotBlank
        private String userId;

        /**
         * key: "yyyy-MM-dd"
         * value: ["breakfast","lunch","dinner"] 중 일부
         */
        @NotNull
        private Map<String, List<String>> requestPayload;

        public String getUserId() { return userId; }
        public Map<String, List<String>> getRequestPayload() { return requestPayload; }

        public void setUserId(String userId) { this.userId = userId; }
        public void setRequestPayload(Map<String, List<String>> requestPayload) { this.requestPayload = requestPayload; }
    }
}
