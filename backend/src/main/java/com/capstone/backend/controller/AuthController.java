package com.capstone.backend.controller;

import java.util.Map;

import com.capstone.backend.domain.User;
import com.capstone.backend.domain.Address;
import com.capstone.backend.dto.LoginResponse;
import com.capstone.backend.dto.RegisterRequest;
import com.capstone.backend.dto.LoginRequest;
import com.capstone.backend.dto.AddressRequest;
import com.capstone.backend.dto.AddressResponse;
import com.capstone.backend.repository.UserRepository;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.UserService;
import com.capstone.backend.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest req){
          try {
            userService.register(req);
            return ResponseEntity.ok(new ApiResponse<>(true, "회원가입 성공", null));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/users/{userid}/addresses")
    public ResponseEntity<?> list(@PathVariable String userid) {
        var user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

        var list = user.getAddresses().stream()
            .sorted((a, b) -> Boolean.compare(!a.isDefault(), !b.isDefault())) // 기본주소 먼저
            .map(AddressResponse::from)
            .toList();

        return ResponseEntity.ok(list);
    }


    @PostMapping("/users/{userid}/addresses")
    public ResponseEntity<?> add(@PathVariable String userid,
                                @RequestBody AddressRequest req) {
        log.info("[주소 추가 요청] userId={}, req={}", userid, req);
        var saved = userService.addAddress(userid, req);
        // 목록 반환 or 생성 리소스 반환
        var list = saved.getAddresses().stream().map(AddressResponse::from).toList();
        log.info("[주소 추가 완료] userId={}, totalAddresses={}", userid, list.size());
        return ResponseEntity.status(201).body(list);
    }

    @GetMapping("/users/{userid}/addresses/current")
    public ResponseEntity<?> current(@PathVariable String userid) {
        log.info("[대표 주소 조회 요청] userId={}", userid);
        var user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

        var cur = user.getAddresses().stream()
            .filter(Address::isDefault)
            .findFirst()
            .orElse(null);

        log.info("[대표 주소 조회 응답] userId={}, currentAddress={}", userid, cur == null ? "없음" : cur);
        return ResponseEntity.ok(cur == null ? null : AddressResponse.from(cur));
    }

    @PutMapping("/users/{userid}/addresses/current")
    public ResponseEntity<?> changeCurrent(@PathVariable String userid,
                                        @RequestBody AddressRequest req) {
        log.info("[대표 주소 변경 요청] userId={}, req={}", userid, req);
        var saved = userService.changeCurrentAddress(userid, req);
        var cur = saved.getAddresses().stream().filter(Address::isDefault).findFirst().orElse(null);
        log.info("[대표 주소 변경 완료] userId={}, newCurrent={}", userid, cur == null ? "없음" : cur);
        return ResponseEntity.ok(cur == null ? null : AddressResponse.from(cur));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        MediaType mediaTypeUtf8 = new MediaType("application", "json", StandardCharsets.UTF_8);
        try {
            User user = userService.login(
                request.getUserid(),
                request.getPassword()
            );

            String token = jwtTokenProvider.createToken(user.getUserid());

            LoginResponse responseData = new LoginResponse(
                user.getUserid(),
                user.getUsername(),
                token,
                user.isHealthInfoSubmitted()
            );
            
            return ResponseEntity
                .ok()
                .contentType(mediaTypeUtf8)
                .body(new ApiResponse<>(true, "로그인 성공", responseData));

        } catch (RuntimeException e){            
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(mediaTypeUtf8)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/check-id")
public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkId(@RequestParam String userid) {
    boolean available = !userService.isIdTaken(userid);
    Map<String, Boolean> result = Map.of("available", available);
    return ResponseEntity.ok(new ApiResponse<>(true, "아이디 중복 확인 완료", result));
}

}