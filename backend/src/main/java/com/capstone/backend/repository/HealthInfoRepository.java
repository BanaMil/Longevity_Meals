package com.capstone.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.capstone.backend.domain.HealthInfo;
import java.util.Optional;

@Repository
public interface HealthInfoRepository extends MongoRepository<HealthInfo, String>{
    Optional<HealthInfo> findByUserid(String userid);
}
