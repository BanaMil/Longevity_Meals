package com.capstone.backend.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.capstone.backend.domain.Disease;

public interface DiseaseRepository extends MongoRepository<Disease, String> {
    Optional<Disease> findByName(String name);
    List<Disease> findByAliasesContaining(String keyword);
}
