package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter @Setter
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String userid; 

    @NotBlank
    private String password;

    @NotBlank
    private String birthdate;

    @NotBlank
    private String phone;

    @NotBlank
    private String address;
    
    // 정책에 맞춘 신필드들
    @NotBlank           // 대표 주소는 도로명
    private String addressRoad;

    private String addressJibun;

    // 권장 required였지만, 기존/마이그레이션 호환 위해 optional로 두고
    // 운영 중 안정화 후 @NotBlank로 격상 권장
    private String postCode;

    private String addressDetail; // 상세주소 (동/호 등)
}
