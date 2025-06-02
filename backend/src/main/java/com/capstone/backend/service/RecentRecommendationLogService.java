package com.capstone.backend.service;

import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.dto.FoodWithIntake;
import com.capstone.backend.repository.MealRecommendationLogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecentRecommendationLogService {

    private final MongoTemplate mongoTemplate;
    private final MealRecommendationLogRepository logRepository;

    public Set<String> getLastTwoDaysFoodNames(String userId) {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        List<MealRecommendationLog> logs = logRepository.findByUserIdAndDateAfter(userId, twoDaysAgo);

        Set<String> result = new HashSet<>();
        for (MealRecommendationLog log : logs) {
            if (log.getBreakfast() != null) result.addAll(log.getBreakfast().stream().map(FoodWithIntake::getName).toList());
            if (log.getLunch() != null) result.addAll(log.getLunch().stream().map(FoodWithIntake::getName).toList());
            if (log.getDinner() != null) result.addAll(log.getDinner().stream().map(FoodWithIntake::getName).toList());
        }
        return result;
    }


    public boolean existsByUserIdAndDate(String userId, LocalDate date) {
        return logRepository.existsByUserIdAndDate(userId, date);
    }

    public void save(MealRecommendationLog log) {
        logRepository.save(log);
    }

    
    public List<MealRecommendationLog> findLatestWeeklyLogs(String userId) {
        // 날짜 기준 내림차순 정렬 → 최근 7개만 가져옴
        Query query = new Query(Criteria.where("userId").is(userId))
            .with(Sort.by(Sort.Direction.DESC, "date"))
            .limit(7);

        List<MealRecommendationLog> logs = mongoTemplate.find(query, MealRecommendationLog.class, "meal_recommendation_logs");

        // 날짜 오름차순으로 정렬 (월→일 순서로 맞추기 위해)
        logs.sort(Comparator.comparing(MealRecommendationLog::getDate));
        return logs;
    }

    public Optional<MealRecommendationLog> findByUserIdAndDate(String userId, LocalDate date) {
        return logRepository.findByUserIdAndDate(userId, date);
    }


}