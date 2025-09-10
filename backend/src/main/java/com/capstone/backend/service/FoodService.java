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
import java.util.HashMap;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class FoodService {

    // MongoDB Document → Food 객체 변환
    private Food convertDocumentToFood(org.bson.Document doc) {
        Food food = new Food();
        food.setName(doc.getString("식품명"));
        food.setOrigin(doc.getString("식품기원명"));
        food.setCategory(doc.getString("식품대분류명"));
        food.setImageUrl(doc.getString("image_url"));
        food.setBaseAmountRaw(doc.getString("영양성분함량기준량"));

        // nutrients 매핑: 영양소 관련 필드만 Map<String, Double>으로 추출
        java.util.Map<String, Double> nutrientMap = new java.util.HashMap<>();
        for (String key : doc.keySet()) {
            if (key.matches(".*\\(.*\\).*") || key.contains("에너지") || key.contains("단백질") || key.contains("지방") || key.contains("탄수화물") || key.contains("칼슘") || key.contains("철") || key.contains("칼륨") || key.contains("나트륨") || key.contains("비타민") || key.contains("콜레스테롤") || key.contains("포화지방산") || key.contains("트랜스지방산") || key.contains("엽산") || key.contains("마그네슘") ) {
                Object val = doc.get(key);
                Double d = null;
                if (val instanceof Number) {
                    d = ((Number) val).doubleValue();
                } else if (val instanceof String str) {
                    try {
                        String numeric = str.replaceAll("[^\\d.]+", "");
                        if (!numeric.isBlank()) d = Double.parseDouble(numeric);
                    } catch (NumberFormatException ignored) {}
                }
                if (d != null) nutrientMap.put(key, d);
            }
        }
        food.setNutrients(nutrientMap);

        // ingredients 매핑: "재료" 필드(List<Document> → List<Ingredient>)
        java.util.List<org.bson.Document> ingDocs = doc.getList("재료", org.bson.Document.class);
        java.util.List<com.capstone.backend.domain.Ingredient> ingredients = new java.util.ArrayList<>();
        if (ingDocs != null) {
            for (org.bson.Document ingDoc : ingDocs) {
                String iname = ingDoc.getString("name");
                String iamount = ingDoc.getString("amount");
                ingredients.add(new com.capstone.backend.domain.Ingredient(iname, iamount));
            }
        }
        food.setIngredients(ingredients);

        // recipe 매핑: "레시피 1", "레시피 2" ... 를 한 줄씩 합침
        StringBuilder recipeBuilder = new StringBuilder();
        int recipeIdx = 1;
        while (true) {
            String key = "레시피 " + recipeIdx;
            if (!doc.containsKey(key)) break;
            String step = doc.getString(key);
            if (step != null && !step.isBlank()) {
                if (recipeBuilder.length() > 0) recipeBuilder.append("\n");
                recipeBuilder.append(step);
            }
            recipeIdx++;
        }
        food.setRecipe(recipeBuilder.toString());

        return food;
    }

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



    public Food findByName(String name) {
        return foodRepository.findFirstByName(name)
            .map(food -> {
                // DB 구조에 맞춰 정확한 영양소 key로 nutrients를 매핑
                HashMap<String, Double> nutrientMap = new HashMap<>();
                String[] nutrientKeys = {
                    "에너지(kcal)", "단백질(g)", "지방(g)", "탄수화물(g)", "당류(g)", "식이섬유(g)",
                    "칼슘(mg)", "철(mg)", "칼륨(mg)", "나트륨(mg)", "비타민 A(μg RAE)", "비타민 C(mg)",
                    "비타민 D(μg)", "콜레스테롤(mg)", "포화지방산(g)", "트랜스지방산(g)",
                    "비타민 B6 (mg)", "비타민 B12(μg)", "엽산(μg DFE)", "불포화지방(g)", "오메가3 지방산(g)", "마그네슘(mg)"
                };
                for (String key : nutrientKeys) {
                    try {
                        java.lang.reflect.Field field = food.getClass().getDeclaredField(key.replaceAll("[() μgRAEDFC-]", "").replaceAll("\\s+", ""));
                        field.setAccessible(true);
                        Object val = field.get(food);
                        Double d = null;
                        if (val instanceof Number) {
                            d = ((Number) val).doubleValue();
                        } else if (val instanceof String str) {
                            try {
                                String numeric = str.replaceAll("[^\\d.]+", "");
                                if (!numeric.isBlank()) d = Double.parseDouble(numeric);
                            } catch (NumberFormatException ignored) {}
                        }
                        if (d != null) nutrientMap.put(key, d);
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {}
                }
                food.setNutrients(nutrientMap);
                // 로그: 영양소 매핑 결과
                org.slf4j.LoggerFactory.getLogger(FoodService.class).info("[findByName] '{}'의 영양소 매핑 결과: {}", food.getName(), nutrientMap);

                // 재료 로그
                org.slf4j.LoggerFactory.getLogger(FoodService.class).info("[findByName] '{}'의 재료: {}", food.getName(), food.getIngredients());
                // 레시피 로그
                org.slf4j.LoggerFactory.getLogger(FoodService.class).info("[findByName] '{}'의 레시피: {}", food.getName(), food.getRecipe());

                return food;
            })
            .orElseGet(() -> {
                Food food = new Food();
                food.setName(name);
                food.setNutrients(new HashMap<>());
                food.setIngredients(new ArrayList<>());
                food.setRecipe("");
                org.slf4j.LoggerFactory.getLogger(FoodService.class).info("[findByName] '{}'의 영양소/재료/레시피 정보 없음 (빈 Food 반환)", name);
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
