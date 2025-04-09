package com.capstone.backend.controller;

import com.capstone.backend.dto.RegisterRequest;
import com.capstone.backend.dto.ApiResponse;
import com.capstone.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request){
          try {
            userService.register(
                request.getUsername(),
                request.getId(),
                request.getPassword(),
                request.getBirthdate()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "회원가입 성공", null));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestParam String username, @RequestParam String password) {
        try {
            userService.login(username, password);
            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", null));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, e.getMessage(), null));
        }


        /*
        boolean success = userService.login(username, password);
        return success ? "로그인 성공!" : "로그인 실패!";*/
    }
}
