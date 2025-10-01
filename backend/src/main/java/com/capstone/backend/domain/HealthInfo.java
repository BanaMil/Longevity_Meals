package com.capstone.backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "health_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthInfo {
    @Id
    private String id;
    
    private String userid;
    private String gender;
    private Double height;
    private Double weight;
    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;
    
    @Field("statusList")  // MongoDB 필드명 명시
    private List<NutrientStatusMapping> statusList;
    
    @Field("personalizedIntake")  // MongoDB 필드명 명시
    private List<PersonalizedIntake> personalizedIntake;
}
