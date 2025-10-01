package com.capstone.backend.dto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressRequest {
    private String addressRoad;
    private String addressJibun;
    private String postCode;
    private String addressDetail;  // ✅ 상세주소
}
