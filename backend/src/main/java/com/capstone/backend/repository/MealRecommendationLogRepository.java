package com.capstone.backend.repository;

import com.capstone.backend.domain.MealRecommendationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRecommendationLogRepository extends MongoRepository<MealRecommendationLog, String> {
    List<MealRecommendationLog> findByUserIdAndDateAfter(String userId, LocalDate after);
}
