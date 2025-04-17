package com.capstone.backend.service;

import java.time.LocalDate;

import com.capstone.backend.domain.User;
import com.capstone.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String username, String id, String password, String birthdate) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        String encodedPassword = encoder.encode(password);
        LocalDate parsedBirthdate = LocalDate.parse(birthdate);

        User user = User.builder()
                        .username(username)
                        .id(id)
                        .password(encodedPassword)
                        .birthdate(parsedBirthdate)
                        .build();
        userRepository.save(user);
    }

    public boolean login(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public boolean isIdTaken(String id) {
        return userRepository.existsById(id);
    }
}
