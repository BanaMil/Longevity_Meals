package com.capstone.backend.controller;

import com.capstone.backend.domain.Food;
import com.capstone.backend.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class RecipeSearch {

    private final FoodService foodService;

    // 1. 음식명으로 검색
    @GetMapping("/search")
    public List<Food> searchByName(@RequestParam String name) {
        return foodService.findByNames(List.of(name));
    }

    // 2. 재료명(단일)으로 검색
    @GetMapping("/search-by-ingredient")
    public List<Food> searchByIngredient(@RequestParam String ingredient) {
        return foodService.findByIngredientName(ingredient);
    }

    // 3. 여러 재료명(OR 조건)으로 검색
    @PostMapping("/search-by-ingredients")
    public List<Food> searchByIngredients(@RequestBody List<String> ingredients) {
        return foodService.findByIngredientNames(ingredients);
    }

    // 4. 음식 상세 조회 (이름으로)
    @GetMapping("/{name}")
    public Food getFoodDetail(@PathVariable String name) {
        return foodService.findByName(name);
    }
}

/* 음식명으로 검색: /api/foods/search?name=음식명
재료로 검색: /api/foods/search-by-ingredient?ingredient=재료명
여러 재료로 검색: /api/foods/search-by-ingredients (POST, body에 재료명 리스트)
음식 상세 조회: /api/foods/{name}
이 API들은 음식의 영양소, 재료, 레시피까지 모두 반환하도록 구현되어 있습니다. */