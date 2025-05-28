package com.capstone.backend.service;

import com.capstone.backend.domain.MealRecommendationLog;
import com.capstone.backend.repository.MealRecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecentRecommendationLogService {

    private final MealRecommendationLogRepository logRepository;

    public Set<String> getLastTwoDaysFoodNames(String userId) {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        List<MealRecommendationLog> logs = logRepository.findByUserIdAndDateAfter(userId, twoDaysAgo);

        Set<String> result = new HashSet<>();
        for (MealRecommendationLog log : logs) {
            result.addAll(log.getFoodNames());
        }
        return result;
    }
}
