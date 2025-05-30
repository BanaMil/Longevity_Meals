package com.capstone.backend.controller;

import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.service.MealPlanService;
import com.capstone.backend.dto.RecommendRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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


    @GetMapping("/weekly")
    public Map<String, DailyMealsResponse> getWeeklyMeals(@RequestParam String userId) {
        Map<String, DailyMeals> rawMeals = mealPlanService.recommendMealForUser(userId);
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

}
