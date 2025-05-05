// 사용자가 로그인할 때 프론트엔드에 전송할 id, 이름, 주소 데이터
package com.capstone.backend.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String id;
    private String username;
    private String address;
    private String token;

    public LoginResponse(String id, String username, String address, String token) {
        this.id = id;
        this.username = username;
        this.address = address;
        this.token = token;
    }
}
