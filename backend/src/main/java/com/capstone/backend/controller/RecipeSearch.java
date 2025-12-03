package com.capstone.backend.controller;

import com.capstone.backend.domain.Food;
import com.capstone.backend.dto.FoodItemResponse;
import com.capstone.backend.service.FoodService;
import com.capstone.backend.mapper.MealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Slf4j
public class RecipeSearch {

    private final FoodService foodService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) {
        try {
            List<Food> foods;

            // 1) 콤마 또는 개행이 있으면 여러 재료 AND 검색
            if (query.contains(",") || query.contains("\n")) {
                List<String> ingredients = List.of(query.split("[,\\n]"))
                                               .stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
                foods = foodService.findByAllIngredientNames(ingredients);
            }
            // 2) 공백 포함: 이름 우선, 실패 시 공백 분할 재료 AND 검색
            else if (query.contains(" ")) {
                foods = foodService.findByNames(List.of(query));
                if (foods.isEmpty()) {
                    List<String> ingredients = List.of(query.split("\\s+"))
                                                   .stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
                    foods = foodService.findByAllIngredientNames(ingredients);
                }
            }
            // 3) 단일어: 음식명 → 음식명에 포함된 음식 → 재료
            else {
                foods = foodService.findByNames(List.of(query));
                if (foods.isEmpty()) {
                    // 음식명에 단어가 포함된 음식과 재료에 포함된 음식 합치기
                    List<Food> nameContains = foodService.findByNameContains(query);
                    List<Food> ingredientFoods = foodService.findByIngredientName(query);

                    // 중복 제거 (음식명 기준)
                    java.util.Set<String> names = new java.util.HashSet<>();
                    List<Food> merged = new java.util.ArrayList<>();
                    for (Food f : nameContains) {
                        if (names.add(f.getName())) merged.add(f);
                    }
                    for (Food f : ingredientFoods) {
                        if (names.add(f.getName())) merged.add(f);
                    }
                    foods = merged;
                }
            }

            if (foods == null || foods.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(java.util.Map.of("message", "정보가 없습니다."));
            }

            for (Food food : foods) {
                log.info("[음식 조회] name='{}' ingredients={} recipeStepsCount={}", food.getName(),
                         food.getIngredients(), food.getRecipe() == null ? 0 : food.getRecipe().size());
            }

            List<FoodItemResponse> result = foods.stream()
                                                 .map(MealMapper::toResponse)
                                                 .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[ERROR] /api/foods/search: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Food> getFoodDetail(@PathVariable String name) {
        try {
            Food food = foodService.findByName(name);
            return ResponseEntity.ok(food);
        } catch (Exception e) {
            log.error("[ERROR] /api/foods/{}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

/* 콤마/개행 → 여러 재료
   공백 → 음식명 우선, 없으면 공백 분할 재료
   단일어 → 음식명 우선, 없으면 단일 재료
   결과는 음식 리스트(JSON)로 반환, 없으면 404
*/