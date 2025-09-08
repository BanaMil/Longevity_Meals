package com.capstone.backend.repository;

import com.capstone.backend.domain.DailyMeals;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DailyMealsRepository extends MongoRepository<DailyMeals, String> {
    List<DailyMeals> findByUserId(String userid);
        // DailyMealsRepository에 정렬 메서드 추가
    List<DailyMeals> findByUserIdOrderByDateDesc(String userId);
}
