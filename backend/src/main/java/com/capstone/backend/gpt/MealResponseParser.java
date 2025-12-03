package com.capstone.backend.gpt;

import com.capstone.backend.dto.DailyMealsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class MealResponseParser {

    private ObjectMapper objectMapper = new ObjectMapper();

    public DailyMealsResponse parse(String jsonResponse) {
    try {
        return objectMapper.readValue(jsonResponse, DailyMealsResponse.class);
    } catch (Exception e) {
        throw new RuntimeException("GPT 응답 파싱 실패", e);
    }
}

}
