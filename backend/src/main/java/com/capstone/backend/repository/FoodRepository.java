package com.capstone.backend.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.capstone.backend.domain.Food;

public interface FoodRepository extends MongoRepository<Food, String> {
    Optional<Food> findByName(String name);
}
