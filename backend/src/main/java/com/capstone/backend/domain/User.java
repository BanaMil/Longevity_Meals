package com.capstone.backend.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "users") // MongoDB의 "users" 컬렉션에 매핑
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String userid;
    private String username;
    private String password;
    private LocalDate birthdate;
    private String phone;
    
    // 주소 정책 필드
    private String address;       // 호환(대표) = addressRoad
    private String addressRoad;   // 대표(도로명)
    private String addressJibun;  // 보조(지번)
    private String postCode;      // 우편번호
    private String addressDetail; // 상세주소 (동/호 등)
    
    @Builder.Default
    private boolean healthInfoSubmitted = false;

    @Builder.Default
    private List<Address> addresses = new java.util.ArrayList<>();

    public boolean isHealthInfoSubmitted() {
    return healthInfoSubmitted;
}

}
