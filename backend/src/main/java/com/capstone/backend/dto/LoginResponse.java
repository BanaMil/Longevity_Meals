// 사용자가 로그인할 때 프론트엔드에 전송할 id, 이름, 주소 데이터
package com.capstone.backend.dto;


import com.capstone.backend.domain.Address;
import lombok.Getter;
import java.util.List;

@Getter
public class LoginResponse {
    private String userid;
    private String username;
    private String address;
    private String addressJibun;
    private String addressRoad;
    private String addressDetail;
    private String postCode;
    private List<Address> addresses;
    private String token;
    private boolean healthInfoSubmitted;

    public LoginResponse(String userid, String username, String address, String addressJibun, String addressRoad, String postCode, String addressDetail, List<Address> addresses, String token, boolean healthInfoSubmitted) {
        this.userid = userid;
        this.username = username;
        this.address = address;
        this.addressJibun = addressJibun;
        this.addressRoad = addressRoad;
        this.postCode = postCode;
        this.addressDetail = addressDetail;
        this.addresses = addresses;
        this.token = token;
        this.healthInfoSubmitted = healthInfoSubmitted;
    }
}
