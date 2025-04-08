package com.capstone.backend.repository;

import com.capstone.backend.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.lang.String;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
