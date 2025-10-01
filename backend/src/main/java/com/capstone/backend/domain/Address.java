package com.capstone.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
@Data
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Address {
    @Id
    private String id;               // MongoDB ObjectId
    private String userId;           // 사용자 ID 참조
    private String addressRoad;      // 도로명
    private String addressJibun;     // 지번
    private String postCode;         // 우편번호
    private String addressDetail;    // 상세주소
    private boolean isDefault;       // 기본 배송지 여부
}
