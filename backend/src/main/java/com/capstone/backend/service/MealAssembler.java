package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.dto.FoodWithIntake;
import com.capstone.backend.dto.ResolvedMealResponse;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MealAssembler {

    public ResolvedMealResponse assemble(DailyMealsResponse names, List<Food> foods) {
        // 1. 이름 → Food 객체 매핑
        Map<String, Food> foodMap = foods.stream()
        .collect(Collectors.toMap(
            Food::getName,
            f -> f,
            (existing, duplicate) -> existing  // 동일 이름이 여러 개일 때 기존 값 유지
        ));

        // 2. 끼니별 음식 이름 리스트를 Food 객체 리스트로 변환
        List<Food> breakfast = mapToFoodList(names.getBreakfast(), foodMap);
        List<Food> lunch = mapToFoodList(names.getLunch(), foodMap);
        List<Food> dinner = mapToFoodList(names.getDinner(), foodMap);

        return new ResolvedMealResponse(
            names.getDate(),
            breakfast,
            lunch,
            dinner
        );
    }

    private List<Food> mapToFoodList(List<FoodWithIntake> items, Map<String, Food> foodMap) {
        List<Food> result = new ArrayList<>();
        for (FoodWithIntake item : items) {
            String name = item.getName();
            Food food = foodMap.get(name);
            if (food == null) {
                throw new NoSuchElementException("DB에서 '" + name + "' 음식 정보를 찾을 수 없습니다.");
            }
            result.add(food);
        }
        return result;
    }

}
