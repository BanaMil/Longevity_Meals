package com.capstone.backend.service;

import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.domain.enums.DeliveryStatus;
import com.capstone.backend.repository.MealRecommendationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DeliveryService {

	@Autowired
	private MealRecommendationLogRepository mealRecommendationLogRepository;

	// 배송 신청
	public boolean requestDelivery(String userId, LocalDate date) {
		Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
		if (logOpt.isPresent()) {
			MealRecommendationLog log = logOpt.get();
			log.setDeliveryRequested(true);
			log.setDeliveryStatus(DeliveryStatus.REQUESTED);
			mealRecommendationLogRepository.save(log);
			return true;
		}
		return false;
	}

	// 배송 상태 조회
	public DeliveryStatus getDeliveryStatus(String userId, LocalDate date) {
		Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
		return logOpt.map(MealRecommendationLog::getDeliveryStatus).orElse(DeliveryStatus.NONE);
	}

	// 배송 상태 변경 (관리자/시스템용)
	public boolean updateDeliveryStatus(String userId, LocalDate date, DeliveryStatus status) {
		Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
		if (logOpt.isPresent()) {
			MealRecommendationLog log = logOpt.get();
			log.setDeliveryStatus(status);
			mealRecommendationLogRepository.save(log);
			return true;
		}
		return false;
	}
}
