package com.capstone.backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;

@Document(collection = "health_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HealthInfo {
    @Id
    private String id;

    private String userid;
    private double height;
    private double weight;
    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;
}
