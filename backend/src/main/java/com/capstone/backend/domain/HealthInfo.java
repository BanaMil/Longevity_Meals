package com.capstone.backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.*;

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

    @Indexed(unique = true) // ensure only one HealthInfo per userid in MongoDB
    private String userid;
    private String gender;
    private Double height;
    private Double weight;
    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;

    @Field("statusList")
    private List<NutrientStatusMapping> statusList;

    @Field("personalizedIntake")
    private List<PersonalizedIntake> personalizedIntake;
}
