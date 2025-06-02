package com.capstone.backend.controller;

import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.service.MealPlanService;
import com.capstone.backend.dto.RecommendRequest;
import com.capstone.backend.dto.TodayMealResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping("/recommend")
    public ResponseEntity<?> requestMealRecommendation(@RequestBody RecommendRequest request) {
        String userId = request.getUserid();

        boolean alreadyExists = mealPlanService.hasExistingMealPlan(userId);
        if (alreadyExists) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("이미 추천된 식단이 존재합니다.");
        }

        mealPlanService.createAndSaveWeeklyMeal(userId);
        return ResponseEntity.ok("식단이 성공적으로 추천되었습니다.");
    }


    @GetMapping("/weekly/{userId}")
    public Map<String, DailyMealsResponse> getWeeklyMeals(@PathVariable String userId) {
        log.info("=== [식단 불러오기 요청] userId: {}", userId);
        
        // ✅ 저장된 추천 기록 조회
        Map<String, DailyMeals> rawMeals = mealPlanService.loadSavedWeeklyMeals(userId);
        log.info("=== [조회된 날짜 수]: {}", rawMeals.size());
        Map<String, DailyMealsResponse> result = new HashMap<>();

        for (Map.Entry<String, DailyMeals> entry : rawMeals.entrySet()) {
            DailyMeals daily = entry.getValue();
            DailyMealsResponse response = new DailyMealsResponse();
            response.setBreakfast(daily.getBreakfast());
            response.setLunch(daily.getLunch());
            response.setDinner(daily.getDinner());
            result.put(entry.getKey(), response);
        }

        return result;
    }
    
    @GetMapping("/today")
    public TodayMealResponse getTodayMeal(
        @RequestParam String userId
    ) {
        return mealPlanService.getTodayMeal(userId);
    }

}
