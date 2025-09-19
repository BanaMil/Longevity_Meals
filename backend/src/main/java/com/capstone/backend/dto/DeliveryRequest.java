package com.capstone.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class DeliveryRequest {
    @NotBlank
    private String userId;

    private Map<String, List<String>> requests; // key: yyyy-MM-dd

    public String getUserId() { return userId; }
    public Map<String, List<String>> getRequests() { return requests; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setRequests(Map<String, List<String>> requests) { this.requests = requests; }
}