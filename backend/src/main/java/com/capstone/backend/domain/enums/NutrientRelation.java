package com.capstone.backend.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NutrientRelation {
    RECOMMENDED("\"권장\""),
    RESTRICTED("제한"),
    CAUTION("주의"),
    NEUTRAL("중립");

    private final String label;

    NutrientRelation(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static NutrientRelation fromLabel(String input) {
        for (NutrientRelation r : values()) {
            if (r.label.equals(input)) {
                return r;
            }
        }
        return NEUTRAL;
    }
}
