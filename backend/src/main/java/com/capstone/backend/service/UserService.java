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

    public void register(
        String username, 
        String userid, 
        String password, 
        String birthdate, 
        String phone, 
        String address, 
        String addressRoad, 
        String addressJibun, 
        String postCode) {
        if (userRepository.findById(userid).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        String encodedPassword = encoder.encode(password);
        LocalDate parsedBirthdate = LocalDate.parse(birthdate);

        User user = User.builder()
                        .username(username)
                        .userid(userid)
                        .password(encodedPassword)
                        .birthdate(parsedBirthdate)
                        .phone(phone)
                        .address(address)
                        .build();
        userRepository.save(user); //MongoDB에 저장
    }

    public User login(String userid, String password) {
    System.out.println("🔐 로그인 시도: ID = " + userid + ", 입력 PW = " + password);

    return userRepository.findByUserid(userid)
        .map(user -> {
            System.out.println("✅ 사용자 찾음: " + user.getUserid());
            boolean passwordMatches = encoder.matches(password, user.getPassword());
            System.out.println("🔍 비밀번호 일치 여부: " + passwordMatches);

            if (passwordMatches) {
                return user;
            } else {
                throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        })
        .orElseThrow(() -> {
            System.out.println("❌ 사용자 ID를 찾을 수 없음: " + userid);
            return new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        });
}


    public boolean isIdTaken(String userid) {
        return userRepository.existsByUserid(userid);
    }
}
