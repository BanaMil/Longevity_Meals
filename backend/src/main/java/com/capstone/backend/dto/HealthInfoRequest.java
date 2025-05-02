package com.capstone.backend.dto;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class HealthInfoRequest {
    
    @Positive
    private double height;

    @Positive
    private double weight;

    private List<String> diseases;
    private List<String> allergies;
    private List<String> dislikes;
}
