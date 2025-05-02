package com.capstone.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String username;
    private String id;
    private String password;
    private String birthdate;
    private String phone;
    private String address;
}
