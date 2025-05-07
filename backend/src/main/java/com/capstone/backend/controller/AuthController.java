package com.capstone.backend.controller;

import java.util.Map;

import com.capstone.backend.domain.User;
import com.capstone.backend.dto.LoginResponse;
import com.capstone.backend.dto.RegisterRequest;
import com.capstone.backend.dto.LoginRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.UserService;
import com.capstone.backend.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request){
          try {
            userService.register(
                request.getUsername(),
                request.getId(),
                request.getPassword(),
                request.getBirthdate(),
                request.getPhone(),
                request.getAddress()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "회원가입 성공", null));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(
                request.getId(),
                request.getPassword()
            );

            String token = jwtTokenProvider.createToken(user.getId());

            LoginResponse responseData = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getAddress(),
                token,
                user.isHealthInfoSubmitted()
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", responseData));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/check-id")
public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkId(@RequestParam String id) {
    boolean available = !userService.isIdTaken(id);
    Map<String, Boolean> result = Map.of("available", available);
    return ResponseEntity.ok(new ApiResponse<>(true, "아이디 중복 확인 완료", result));
}

}
