package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.domain.enums.NutrientConstants;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.Document;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final MongoTemplate mongoTemplate;

    public List<Food> fetchFilteredFoods() {
        Query query = new Query();

        // 기본 필드 포함
        query.fields()
            .include("식품명")
            .include("식품기원명")
            .include("식품대분류명")
            .include("영양성분함량기준량");

        // 영양소 필드만 추가적으로 포함
        for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
            query.fields().include(nutrient);
        }

        List<Document> docs = mongoTemplate.find(query, Document.class, "foods");

        List<Food> foods = new ArrayList<>();
        for (Document doc : docs) {
            Map<String, Double> nutrientMap = new HashMap<>();
            for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
                Object val = doc.get(nutrient);
                if (val instanceof Number) {
                    nutrientMap.put(nutrient, ((Number) val).doubleValue());
                }
            }

            Food food = new Food();
            food.setName(doc.getString("식품명"));
            food.setOrigin(doc.getString("식품기원명"));
            food.setCategory(doc.getString("식품대분류명"));
            food.setBaseAmount(doc.getDouble("영양성분함량기준량"));
            food.setNutrients(nutrientMap);

            foods.add(food);
        }

        return foods;
    }
}
