package com.capstone.backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.capstone.backend.domain.enums.DeliveryStatus;
import com.capstone.backend.dto.FoodWithIntake;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "meal_recommendation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecommendationLog {
    @Id
    private String id;
    private String userId;
    private LocalDate date;             // 추천된 날짜
    private List<FoodWithIntake> breakfast;
    private List<FoodWithIntake> lunch;
    private List<FoodWithIntake> dinner;

    // 추가된 필드
    @Field("delivery_breakfast")
    private boolean deliveryBreakfast; // 아침 배송 요청 여부
    @Field("delivery_lunch")
    private boolean deliveryLunch;     // 점심 배송 요청 여부
    @Field("delivery_dinner")
    private boolean deliveryDinner;    // 저녁 배송 요청 여부

    @Field("delivery_breakfast_status")
    private DeliveryStatus deliveryBreakfastStatus; // 아침 배송 상태
    @Field("delivery_lunch_status")
    private DeliveryStatus deliveryLunchStatus;     // 점심 배송 상태
    @Field("delivery_dinner_status")
    private DeliveryStatus deliveryDinnerStatus;    // 저녁 배송 상태

    public MealRecommendationLog(String userid, LocalDate date, List<FoodWithIntake> breakfast, List<FoodWithIntake> lunch, List<FoodWithIntake> dinner) {
        this.userId = userid;
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.deliveryBreakfast = false;
        this.deliveryLunch = false;
        this.deliveryDinner = false;
        this.deliveryBreakfastStatus = DeliveryStatus.NONE;
        this.deliveryLunchStatus = DeliveryStatus.NONE;
        this.deliveryDinnerStatus = DeliveryStatus.NONE;
    }
    
}
