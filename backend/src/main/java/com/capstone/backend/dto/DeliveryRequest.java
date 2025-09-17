package com.capstone.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryRequest {
    @NotBlank
    private String userId;

    @NotNull
    private Map<String, List<MealSlot>> requestPayload; // key: yyyy-MM-dd

    public String getUserId() { return userId; }
    public Map<String, List<MealSlot>> getRequestPayload() { return requestPayload; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setRequestPayload(Map<String, List<MealSlot>> requestPayload) { this.requestPayload = requestPayload; }

    public enum MealSlot {
        BREAKFAST("breakfast"),
        LUNCH("lunch"),
        DINNER("dinner");

        private final String wire;
        MealSlot(String wire) { this.wire = wire; }

        @JsonValue
        public String getWire() { return wire; }

        @JsonCreator
        public static MealSlot from(String s) {
            if (s == null) return null;
            String k = s.trim().toLowerCase(Locale.ROOT);
            return switch (k) {
                case "breakfast" -> BREAKFAST;
                case "lunch"     -> LUNCH;
                case "dinner"    -> DINNER;
                default -> throw new IllegalArgumentException("Invalid meal slot: " + s);
            };
        }
    }
}
