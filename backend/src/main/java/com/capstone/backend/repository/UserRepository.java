package com.capstone.backend.repository;

import com.capstone.backend.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.lang.String;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String Id);
    Optional<User> findByUserid(String Userid);
    boolean existsByUserid(String Userid);
}


//docker exec -it capstone-container bash
//cd /app/backend
//./gradlew test
//./gradlew bootRun 으로 서버 실행