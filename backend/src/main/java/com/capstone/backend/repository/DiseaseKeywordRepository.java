package com.capstone.backend.repository;

import com.capstone.backend.domain.DiseaseKeywordMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DiseaseKeywordRepository extends MongoRepository<DiseaseKeywordMapping, String> {
    
    // 키워드를 정확히 포함하는 경우
    List<DiseaseKeywordMapping> findByKeywordContaining(String keyword);

    // 키워드 완전 일치
    List<DiseaseKeywordMapping> findByKeyword(String keyword);
}