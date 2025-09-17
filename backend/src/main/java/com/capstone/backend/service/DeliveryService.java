package com.capstone.backend.service;

import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.domain.enums.DeliveryStatus;
import com.capstone.backend.dto.DeliveryRequest.MealSlot;
import com.capstone.backend.repository.MealRecommendationLogRepository;

import lombok.RequiredArgsConstructor;

import com.capstone.backend.dto.DeliveryRequest;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final MealRecommendationLogRepository mealRecommendationLogRepository;
	private final MongoTemplate mongoTemplate;

	public void applySelections(DeliveryRequest req) {
        log.info("=== [배송 신청 반영] userId: {}", req.getUserId());
        String userId = req.getUserId();
        Map<String, List<MealSlot>> payload = req.getRequestPayload();

        if (payload == null || payload.isEmpty()) return;

        payload.forEach((dateStr, slots) -> {
            if (dateStr == null || dateStr.isBlank() || slots == null) return;

            Query q = Query.query(
                Criteria.where("userId").is(userId)
                        .and("date").is(LocalDate.parse(dateStr))
            );

            Update u = new Update();

            for (MealSlot slot : slots) {
                switch (slot) {
                    case BREAKFAST -> u.set("delivery_breakfast_status", DeliveryStatus.REQUESTED);
                    case LUNCH     -> u.set("delivery_lunch_status", DeliveryStatus.REQUESTED);
                    case DINNER    -> u.set("delivery_dinner_status", DeliveryStatus.REQUESTED);
                }
            }

            mongoTemplate.upsert(q, u, "meal_recommendation_logs");
        });
    }



	// 배송 상태 조회
	// public DeliveryStatus getDeliveryStatus(String userId, LocalDate date) {
	// 	Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
	// 	return logOpt.map(MealRecommendationLog::getDeliveryStatus).orElse(DeliveryStatus.NONE);
	// }

	// 배송 상태 변경 (관리자/시스템용)
	// public boolean updateDeliveryStatus(String userId, LocalDate date, DeliveryStatus status) {
	// 	Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
	// 	if (logOpt.isPresent()) {
	// 		MealRecommendationLog log = logOpt.get();
	// 		log.setDeliveryStatus(status);
	// 		mealRecommendationLogRepository.save(log);
	// 		return true;
	// 	}
	// 	return false;
	// }
}
