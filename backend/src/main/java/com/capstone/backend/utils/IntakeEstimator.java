package com.capstone.backend.utils;

import java.util.Map;
import static java.util.Map.entry;

public class IntakeEstimator {
    public static final Map<String, Double> WEIGHT_RANGES = Map.ofEntries(
        entry("밥류", 200.0),
        entry("국 및 탕류", 250.0),
        entry("찌개 및 전골류", 200.0),
        entry("김치류", 50.0),
        entry("구이류", 70.0),
        entry("조림류", 60.0),
        entry("볶음류", 70.0),
        entry("젓갈류", 20.0),
        entry("나물∙숙채류", 60.0),
        entry("국밥류", 350.0),
        entry("국수류", 400.0)
    );

    public static double getEstimatedIntake(String category) {
        return WEIGHT_RANGES.getOrDefault(category, 100.0);
    }
}
