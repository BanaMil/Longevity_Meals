package com.capstone.backend.controller;

import com.capstone.backend.domain.enums.DeliveryStatus;
import com.capstone.backend.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

	@Autowired
	private DeliveryService deliveryService;

	// 배송 신청
	@PostMapping("/request")
	public String requestDelivery(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		boolean result = deliveryService.requestDelivery(userId, date);
		return result ? "배송 신청 완료" : "해당 식단 기록이 없습니다.";
	}

	// 배송 상태 조회
	@GetMapping("/status")
	public DeliveryStatus getDeliveryStatus(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return deliveryService.getDeliveryStatus(userId, date);
	}

	// 배송 상태 변경 (관리자/시스템용)
	@PostMapping("/update-status")
	public String updateDeliveryStatus(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam DeliveryStatus status) {
		boolean result = deliveryService.updateDeliveryStatus(userId, date, status);
		return result ? "배송 상태 변경 완료" : "해당 식단 기록이 없습니다.";
	}
}
