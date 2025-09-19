package com.capstone.backend.domain.enums;

public enum DeliveryStatus {
    NONE,       // 배송 미신청
    IN_TRANSIT,  // 배송 신청됨
    PREPARING,  // 준비중
    SHIPPED,    // 배송중
    DELIVERED,  // 배송완료
    CANCELED    // 취소됨
}