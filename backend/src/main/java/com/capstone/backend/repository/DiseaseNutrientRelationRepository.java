package com.capstone.backend.repository;

import com.capstone.backend.domain.DiseaseNutrientRelation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import com.capstone.backend.domain.DiseaseNutrientRelation;

public interface DiseaseNutrientRelationRepository extends MongoRepository<DiseaseNutrientRelation, String> {
    List<DiseaseNutrientRelation> findByDisease(String disease);
}
