package com.capstone.backend.service;

import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.domain.enums.DeliveryStatus;
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
import java.time.ZoneId;
import java.util.Date;
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
        Map<String, List<String>> requests = req.getRequests();

        // requests 전체 로그
        log.info("[배송 신청] requests: {}", requests);

        if (requests == null || requests.isEmpty()) return;

        requests.forEach((dateStr, slots) -> {
            if (dateStr == null || dateStr.isBlank() || slots == null) return;

            // 각 날짜별 MealSlot 로그
            log.info("[배송 신청] 날짜: {}, MealSlots: {}", dateStr, slots);

            Query q = Query.query(
                Criteria.where("userId").is(userId)
                        .and("date").is(
                            Date.from(LocalDate.parse(dateStr)
                            .atStartOfDay(java.time.ZoneId.systemDefault()
                            ).toInstant())
                        )
            );

            Update u = new Update();

            for (String slot : slots) {
                log.info("[배송 신청] 날짜: {}, MealSlot: {}", dateStr, slot); // 각 MealSlot 로그
                switch (slot) {
                    case "breakfast" -> u.set("delivery_breakfast", true);
                    case "lunch"     -> u.set("delivery_lunch", true);
                    case "dinner"    -> u.set("delivery_dinner", true);
                }
            }

            mongoTemplate.upsert(q, u, "meal_recommendation_logs");
        });
    }



	// 배송 상태 조회 (아침/점심/저녁 각각 반환)
	public Map<String, DeliveryStatus> getDeliveryStatus(String userId, LocalDate date) {
		Optional<MealRecommendationLog> logOpt = mealRecommendationLogRepository.findByUserIdAndDate(userId, date);
		Map<String, DeliveryStatus> statusMap = new java.util.HashMap<>();
		if (logOpt.isPresent()) {
			MealRecommendationLog log = logOpt.get();
			statusMap.put("breakfast", log.getDeliveryBreakfastStatus());
			statusMap.put("lunch", log.getDeliveryLunchStatus());
			statusMap.put("dinner", log.getDeliveryDinnerStatus());
		} else {
			statusMap.put("breakfast", DeliveryStatus.NONE);
			statusMap.put("lunch", DeliveryStatus.NONE);
			statusMap.put("dinner", DeliveryStatus.NONE);
		}
		return statusMap;
	}

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
