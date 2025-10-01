package com.capstone.backend.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {
    private String addressRoad;      // 도로명
    private String addressJibun;     // 지번
    private String postCode;         // 우편번호
    private String addressDetail;    // ✅ 상세주소 (동/호 등) - 이름 통일
    private boolean isDefault;       // 기본 배송지 여부
}
