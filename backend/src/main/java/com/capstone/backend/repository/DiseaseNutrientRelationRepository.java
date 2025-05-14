package com.capstone.backend.repository;

import com.capstone.backend.domain.DiseaseKeywordMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DiseaseNutrientRelationRepository extends MongoRepository<DiseaseNutrientRelation, String> {
    List<DiseaseNutrientRelation> findByDisease(String disease);
}
