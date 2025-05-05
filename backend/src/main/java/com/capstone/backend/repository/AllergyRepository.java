package com.capstone.backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.capstone.backend.domain.Allergy;

public interface AllergyRepository extends MongoRepository<Allergy, String> {
    Optional<Allergy> findByName(String name);
}
