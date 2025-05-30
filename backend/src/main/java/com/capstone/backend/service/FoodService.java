package com.capstone.backend.service;

import com.capstone.backend.domain.Food;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final MongoTemplate mongoTemplate;

    public List<Food> fetchFilteredFoods() {
        return mongoTemplate.findAll(Food.class, "foodDB");
    }
}
