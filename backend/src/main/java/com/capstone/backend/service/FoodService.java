package com.capstone.backend.service;

import com.capstone.backend.domain.Food;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final MongoTemplate mongoTemplate;

    public List<Food> fetchFilteredFoods() {
        Query query = new Query();

        // 기본 필드
        query.fields()
            .include("식품명")
            .include("식품기원명")
            .include("식품대분류명");

        // 주요 영양소만 선택적으로 include
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
                }
            }

            Food food = new Food();
            food.setName(doc.getString("식품명"));
            food.setOrigin(doc.getString("식품기원명"));
            food.setCategory(doc.getString("식품대분류명"));
            food.setNutrients(nutrientMap);

            foods.add(food);
        }

        return foods;
    }

}
