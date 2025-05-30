package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.domain.enums.NutrientConstants;
import com.capstone.backend.repository.FoodRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.Document;

import java.util.List; 
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final MongoTemplate mongoTemplate;
    private final FoodRepository foodRepository;

    public List<Food> fetchFilteredFoods() {
        Query query = new Query();

        // 기본 필드
        query.fields()
            .include("식품명")
            .include("식품기원명")
            .include("식품대분류명")
            .include("영양성분함량기준량");

        // 주요 영양소 필드 포함
        for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
            query.fields().include(nutrient);
        }

        List<Document> docs = mongoTemplate.find(query, Document.class, "foodDB");
        List<Food> foods = new ArrayList<>();

        for (Document doc : docs) {
            Map<String, Double> nutrientMap = new HashMap<>();

            for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
                Object val = doc.get(nutrient);

                if (val instanceof Number number) {
                    nutrientMap.put(nutrient, number.doubleValue());
                } else if (val instanceof String str) {
                    try {
                        String numeric = str.replaceAll("[^\\d.]+", "");  // 숫자만 추출
                        if (!numeric.isBlank()) {
                            nutrientMap.put(nutrient, Double.parseDouble(numeric));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[⚠️] 영양소 파싱 실패: " + nutrient + " → " + str);
                    }
                }
            }

            // ✅ baseAmount 파싱
            double parsedBaseAmount = 0.0;
            Object baseVal = doc.get("영양성분함량기준량");
            if (baseVal != null) {
                try {
                    if (baseVal instanceof Number num) {
                        parsedBaseAmount = num.doubleValue();
                    } else if (baseVal instanceof String str) {
                        String numeric = str.replaceAll("[^\\d.]+", "").trim();
                        if (!numeric.isEmpty()) {
                            parsedBaseAmount = Double.parseDouble(numeric);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[⚠️] baseAmount 파싱 실패: " + baseVal);
                }
            }

            Food food = new Food();
            food.setName(doc.getString("식품명"));
            food.setOrigin(doc.getString("식품기원명"));
            food.setCategory(doc.getString("식품대분류명"));
            food.setNutrients(nutrientMap);
            food.setBaseAmount(parsedBaseAmount);  // ✅ 적용
            foods.add(food);
        }

        return foods;
    }


    public Food findByName(String name) {
        return foodRepository.findFirstByName(name)
            .orElseThrow(() -> new NoSuchElementException("음식명으로 Food를 찾을 수 없습니다: " + name));
    }

}
