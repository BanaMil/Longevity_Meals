package com.capstone.backend.dto;

import lombok.Setter;

import java.util.List;

@Setter
public class MealResponse {
    List<MealItemResponse> rice;
    List<MealItemResponse> soup;
    List<MealItemResponse> sides;
}
