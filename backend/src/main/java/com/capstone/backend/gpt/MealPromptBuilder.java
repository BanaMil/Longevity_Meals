package com.capstone.backend.gpt;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.Food;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealPromptBuilder {
    public String buildPrompt(HealthInfo user, List<Food> foods) {
        // 사용자와 음식 정보를 자연어 프롬프트로 구성
        return "...";
    }
}
