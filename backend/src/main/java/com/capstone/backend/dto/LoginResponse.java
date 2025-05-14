// 사용자가 로그인할 때 프론트엔드에 전송할 id, 이름, 주소 데이터
package com.capstone.backend.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String userid;
    private String username;
    private String address;
    private String token;
    private boolean healthInfoSubmitted;

    public LoginResponse(String userid, String username, String address, String token, boolean healthInfoSubmitted) {
        this.userid = userid;
        this.username = username;
        this.address = address;
        this.token = token;
        this.healthInfoSubmitted = healthInfoSubmitted;
    }
}
