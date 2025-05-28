package com.capstone.backend.controller;

import com.capstone.backend.domain.DailyMeals;
import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

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
