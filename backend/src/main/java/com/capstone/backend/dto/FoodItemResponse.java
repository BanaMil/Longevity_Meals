package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FoodItemResponse {
    private String name;
    private String imageUrl;
    private List<String> nutrients;     // 이름 목록만
    private List<String> ingredients;
    private String recipe;
}
