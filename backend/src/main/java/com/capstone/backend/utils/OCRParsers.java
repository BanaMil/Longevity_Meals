package com.capstone.backend.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;



// 1) OCR 파싱 유틸 (예: com.capstone.backend.ocr.OcrParsers)
public final class OcrParsers {
    private static final Pattern HEIGHT = Pattern.compile("키\\s*\\(cm\\)\\s*(\\d{2,3}(?:[.,]\\d)?)");
    private static final Pattern WEIGHT = Pattern.compile("몸무게\\s*\\(kg\\)\\s*(\\d{1,3}(?:[.,]\\d)?)");
    private static final Pattern BP     = Pattern.compile("혈압\\s*\\(mmHg\\)\\s*(\\d{2,3})\\s*/\\s*(\\d{2,3})");
    private static final Pattern HBA1C  = Pattern.compile("HbA1c\\s*(\\d{1,2}(?:[.,]\\d)?)");
    // 필요 시 추가: 총콜레스테롤, HDL, LDL, AST, ALT 등

    private static Double parseNumber(String s) {
        if (s == null) return null;
        try { return Double.valueOf(s.replace(',', '.')); } catch (Exception e) { return null; }
    }

    public static Double extractHeightCm(String text) {
        var m = HEIGHT.matcher(text); return m.find() ? parseNumber(m.group(1)) : null;
    }
    public static Double extractWeightKg(String text) {
        var m = WEIGHT.matcher(text); return m.find() ? parseNumber(m.group(1)) : null;
    }
    public static int[] extractBloodPressure(String text) {
        var m = BP.matcher(text); return m.find() ? new int[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))} : null;
    }
    public static Double extractHbA1c(String text) {
        var m = HBA1C.matcher(text); return m.find() ? parseNumber(m.group(1)) : null;
    }
}
