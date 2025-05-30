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

    @Field("식품명")
    private String name;

    @Field("식품기원명")
    private String origin;

    @Field("식품대분류명")
    private String category;

    @Field("영양성분함량기준량")
    private double baseAmount;

    private Map<String, Double> nutrients;

    private List<String> ingredients;
    @Field("image_url")
    private String imageUrl;

    @Field("recipe")
    private String recipe;
}
