package com.capstone.backend.controller;

import com.capstone.backend.domain.Food;
import com.capstone.backend.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class RecipeSearch {

    private final FoodService foodService;

    @GetMapping("/search")
    public ResponseEntity<List<Food>> search(@RequestParam String query) {
        try {
            List<Food> result;

            // 1) 콤마 또는 개행이 있으면 여러 재료 검색
            if (query.contains(",") || query.contains("\n")) {
                List<String> ingredients = List.of(query.split("[,\\n]"))
                                               .stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
                result = foodService.findByIngredientNames(ingredients);
            }
            // 2) 공백 포함: 이름 우선, 실패 시 공백 분할 재료 검색
            else if (query.contains(" ")) {
                result = foodService.findByNames(List.of(query));
                if (result.isEmpty()) {
                    List<String> ingredients = List.of(query.split("\\s+"))
                                                   .stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
                    result = foodService.findByIngredientNames(ingredients);
                }
            }
            // 3) 그 외: 이름 우선, 실패 시 단일 재료 검색
            else {
                result = foodService.findByNames(List.of(query));
                if (result.isEmpty()) {
                    result = foodService.findByIngredientName(query);
                }
            }

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("[ERROR] /api/foods/search: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Food> getFoodDetail(@PathVariable String name) {
        try {
            Food food = foodService.findByName(name);
            return ResponseEntity.ok(food);
        } catch (Exception e) {
            System.out.println("[ERROR] /api/foods/" + name + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

/* 콤마/개행 → 여러 재료
   공백 → 음식명 우선, 없으면 공백 분할 재료
   단일어 → 음식명 우선, 없으면 단일 재료
   결과는 음식 리스트(JSON)로 반환, 없으면 404
*/