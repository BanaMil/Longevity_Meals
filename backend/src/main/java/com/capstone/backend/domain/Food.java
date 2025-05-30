package com.capstone.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.List;


@Document(collection = "foodDB")  // ← 컬렉션 이름도 정확히 확인
@Getter
@Setter
public class Food {
    @Id
    private String id;

    @Field("foodName")
    private String name;

    private String origin;
    private String category;

    @Field("servingSize")
    private double baseAmount;

    private Map<String, Double> nutrients;

    private List<String> ingredients;
    private String imageUrl;

    @Field("recipe")
    private String recipe;
}
