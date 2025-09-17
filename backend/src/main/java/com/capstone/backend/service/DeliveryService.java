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
                        .and("date").is(dateStr) // 날짜는 기존 설계대로 문자열 "yyyy-MM-dd"
            );

            Update u = new Update().set("delivery.updatedAt", Instant.now().toString());
            for (MealSlot slot : slots) {
                switch (slot) {
                    case BREAKFAST -> u.set("delivery.breakfast", true);
                    case LUNCH     -> u.set("delivery.lunch", true);
                    case DINNER    -> u.set("delivery.dinner", true);
                }
            }

            // DailyMeals 엔티티를 사용 중이라면 클래스 타입으로, 컬렉션명을 쓴다면 "dailyMeals"로 교체
            mongoTemplate.upsert(q, u, "dailyMeals");
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
