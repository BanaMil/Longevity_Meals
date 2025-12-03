package com.capstone.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.capstone.backend.domain.NutrientReference;

import java.util.Optional;

@Repository
public interface NutrientReferenceRepository extends MongoRepository<NutrientReference, String> {
    Optional<NutrientReference> findByNutrient(String nutrient);
}
