package com.capstone.backend.domain;

import com.capstone.backend.dto.NutrientIntake;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Document(collection = "foods")
@Getter
@Setter
public class Food {
    @Id
    private String id;

    private String name;                // 식품명
    private String origin;             // 식품기원명
    private String category;           // 식품대분류명
    private double baseAmount;         // 100g 기준 섭취량
    private List<NutrientIntake> nutrients;
    private String imageUrl;
    private List<String> ingredients;
    private String Recipe;


}