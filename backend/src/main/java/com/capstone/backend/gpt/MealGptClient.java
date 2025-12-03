package com.capstone.backend.gpt;


import com.capstone.backend.dto.DailyMealsResponse;
import com.capstone.backend.dto.FoodCandidate;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.dto.MealPlanWeeklyRequest;
import com.capstone.backend.dto.WeeklyMealsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MealGptClient {

    private final RestTemplate restTemplate;

    @Value("${langchain.api.url}")
    private String langchainApiUrl;  // ex) http://localhost:8000/api/gpt/mealplan

    public DailyMealsResponse requestMealPlan(HealthInfoRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HealthInfoRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("보내는 요청 본문: {}", new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("요청 본문 직렬화 중 오류 발생", e);
        }


        ResponseEntity<DailyMealsResponse> response = restTemplate.exchange(
                langchainApiUrl,
                HttpMethod.POST,
                entity,
                DailyMealsResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();  // ✅ 바로 DTO로 반환
        } else {
            throw new RuntimeException("GPT API 호출 실패: " + response.getStatusCode());
        }
    }

    public WeeklyMealsResponse requestWeeklyMealPlan(HealthInfoRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HealthInfoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WeeklyMealsResponse> response = restTemplate.exchange(
            langchainApiUrl + "/weekly",  // ✅ FastAPI 쪽 /weekly endpoint
            HttpMethod.POST,
            entity,
            WeeklyMealsResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("GPT 주간 식단 API 호출 실패: " + response.getStatusCode());
        }
    }

}   
