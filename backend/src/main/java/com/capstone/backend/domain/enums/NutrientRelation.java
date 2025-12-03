package com.capstone.backend.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NutrientRelation {
    RECOMMENDED("권장"),
    RESTRICTED("제한"),
    CAUTION("주의"),
    NEUTRAL("중립");

    private final String label;

    NutrientRelation(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    @JsonValue
    public String toJson() {
        return this.name();  // "RECOMMENDED" 등
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

    // 우선순위가 높은 것을 반환 (RESTRICTED > CAUTION > RECOMMENDED > NEUTRAL)
    public static NutrientRelation higher(NutrientRelation a, NutrientRelation b) {
        if (a == null) return b;
        if (b == null) return a;

        int priorityA = getPriority(a);
        int priorityB = getPriority(b);

        return priorityA >= priorityB ? a : b;
    }

    private static int getPriority(NutrientRelation relation) {
        return switch (relation) {
            case RESTRICTED -> 3;
            case CAUTION -> 2;
            case RECOMMENDED -> 1;
            case NEUTRAL -> 0;
        };
    }
}
