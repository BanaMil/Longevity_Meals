package com.capstone.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class DeliveryRequest {
    @NotBlank
    private String userId;

    private Map<String, List<String>> requestPayload; // key: yyyy-MM-dd

    public String getUserId() { return userId; }
    public Map<String, List<String>> getRequestPayload() { return requestPayload; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setRequestPayload(Map<String, List<String>> requestPayload) { this.requestPayload = requestPayload; }
}