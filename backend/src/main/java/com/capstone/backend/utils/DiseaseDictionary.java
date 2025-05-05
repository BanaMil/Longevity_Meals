package com.capstone.backend.utils;

import java.util.Map;
import java.util.HashMap;

public class DiseaseDictionary {
    private static final Map<String, String> diseaseNameToId = new HashMap<>();

    static {
        diseaseNameToId.put("고혈압", "D001");
        diseaseNameToId.put("당뇨", "D002");
        diseaseNameToId.put("이상지질혈증", "D003");
        diseaseNameToId.put("심부전", "D004");
        // ... 추가 가능
    }

    public static String getDiseaseId(String name) {
        return diseaseNameToId.get(name);
    }

    public static Map<String, String> getAll() {
        return diseaseNameToId;
    }

    public static boolean contains(String name) {
        return diseaseNameToId.containsKey(name);
    }
}
