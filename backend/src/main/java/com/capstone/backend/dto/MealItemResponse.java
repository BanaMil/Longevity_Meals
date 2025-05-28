package com.capstone.backend.dto;

import lombok.Setter;
import lombok.Getter;

import java.util.Map;
import java.util.List;

@Setter
@Getter
public class MealItemResponse {
    private String name;                        // 음식 이름
    private String imageUrl;                    // 음식 사진 (이미지 URL)
    private Map<String, Double> nutrients;      // {"단백질": 12.0, "칼슘": 80.0, ...}
    private List<String> ingredients;           // ["두부", "간장", "참기름"]
    private String recipe;                      // "두부를 지지고 양념을 넣는다."
}
