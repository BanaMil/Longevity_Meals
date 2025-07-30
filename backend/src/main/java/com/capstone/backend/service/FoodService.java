package com.capstone.backend.service;

import com.capstone.backend.domain.Food;
import com.capstone.backend.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    // ✅ 단일 음식 이름으로 조회
    public Food findByName(String name) {
        return foodRepository.findFirstByName(name)
            .orElseThrow(() -> new NoSuchElementException("음식명을 찾을 수 없습니다: " + name));
    }

    // ✅ 다건 이름으로 조회 (GPT가 여러 음식명을 반환할 경우)
    public List<Food> findByNames(List<String> names) {
        return foodRepository.findByNameIn(names);
    }
}
