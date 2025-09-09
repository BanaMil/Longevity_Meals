package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.bson.Document;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    // private Food convertDocumentToFood(Document doc) {
    //     Map<String, Double> nutrientMap = new HashMap<>();
    //     for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
    //         Object val = doc.get(nutrient);
    //         if (val instanceof Number number) {
    //             nutrientMap.put(nutrient, number.doubleValue());
    //         } else if (val instanceof String str) {
    //             try {
    //                 String numeric = str.replaceAll("[^\\d.]+", "");
    //                 if (!numeric.isBlank()) {
    //                     nutrientMap.put(nutrient, Double.parseDouble(numeric));
    //                 }
    //             } catch (NumberFormatException ignored) {}
    //         }
    //     }

    //     double parsedBaseAmount = 0.0;
    //     Object baseVal = doc.get("영양성분함량기준량");
    //     if (baseVal instanceof Number num) {
    //         parsedBaseAmount = num.doubleValue();
    //     } else if (baseVal instanceof String str) {
    //         String numeric = str.replaceAll("[^\\d.]+", "").trim();
    //         if (!numeric.isEmpty()) {
    //             parsedBaseAmount = Double.parseDouble(numeric);
    //         }
    //     }

    //     Food food = new Food();
    //     food.setName(doc.getString("식품명"));
    //     food.setOrigin(doc.getString("식품기원명"));
    //     food.setCategory(doc.getString("식품대분류명"));
    //     food.setBaseAmount(parsedBaseAmount);
    //     food.setNutrients(nutrientMap);
    //     food.setImageUrl(doc.getString("image_url"));
    //     food.setIngredients(doc.getList("ingredients", String.class));
    //     food.setRecipe(doc.getString("recipe"));
    //     return food;
    // }


    // public List<Food> fetchFilteredFoods() {
    //     Query query = new Query();

    //     // 기본 필드
    //     query.fields()
    //         .include("식품명")
    //         .include("식품기원명")
    //         .include("식품대분류명")
    //         .include("영양성분함량기준량");

    //     // 주요 영양소 필드 포함
    //     for (String nutrient : NutrientConstants.TARGET_NUTRIENTS) {
    //         query.fields().include(nutrient);
    //     }

    //     List<Document> docs = mongoTemplate.find(query, Document.class, "foodDB");

    //     return docs.stream()
    //         .map(this::convertDocumentToFood)
    //         .toList();
    // }



    // public Food findByName(String name) {
    //     Query query = new Query(Criteria.where("식품명").is(name));
    //     Document doc = mongoTemplate.findOne(query, Document.class, "foodDB");

    //     if (doc == null) {
    //         throw new NoSuchElementException("음식명으로 Document를 찾을 수 없습니다: " + name);
    //     }

    //     return convertDocumentToFood(doc);
    // }



    
    // ✅ 단일 음식 이름으로 조회
    public Food findByName(String name) {
        return foodRepository.findFirstByName(name)
            .orElseGet(() -> {
                Food food = new Food();
                food.setName(name);
                return food;
            });
    }

    // ✅ 다건 이름으로 조회 (GPT가 여러 음식명을 반환할 경우)
    public List<Food> findByNames(List<String> names) {
        return foodRepository.findByNameIn(names);
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

}
