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
        if (log.getBreakfast() != null) result.addAll(log.getBreakfast());
        if (log.getLunch() != null) result.addAll(log.getLunch());
        if (log.getDinner() != null) result.addAll(log.getDinner());
    }
    return result;
}


    public boolean existsByUserIdAndDate(String userId, LocalDate date) {
        return logRepository.existsByUserIdAndDate(userId, date);
    }

    public void save(MealRecommendationLog log) {
        logRepository.save(log);
    }
}
