package com.capstone.backend.utils;

import java.util.Locale;
import java.util.regex.*;

public final class OcrParsers {
    // 대표 표기 + 대안 키워드(신장/체중)
    private static final Pattern HEIGHT_CM = Pattern.compile("(키\\s*\\(cm\\)|신장)\\s*[:：]?'?\\s*(\\d{2,3}(?:[.,]\\d)?)");
    private static final Pattern WEIGHT_KG = Pattern.compile("(몸무게\\s*\\(kg\\)|체중)\\s*[:：]?'?\\s*(\\d{1,3}(?:[.,]\\d)?)");

    // 성별: 남/여/男/女/남자/여자
    private static final Pattern GENDER = Pattern.compile("성별\\s*[:：]?\\s*([남여男女]|남자|여자)");

    private static Double toDouble(String s) {
        if (s == null) return null;
        try { return Double.valueOf(s.replace(',', '.')); } catch (Exception e) { return null; }
    }

    public static Double extractHeightCm(String text) {
        if (text == null) return null;
        Matcher m = HEIGHT_CM.matcher(text);
        return m.find() ? toDouble(m.group(2)) : null;
    }

    public static Double extractWeightKg(String text) {
        if (text == null) return null;
        Matcher m = WEIGHT_KG.matcher(text);
        return m.find() ? toDouble(m.group(2)) : null;
    }

    /** 내부 표준: "male" / "female" 반환 */
    public static String extractGenderStd(String text) {
        if (text == null) return null;
        Matcher m = GENDER.matcher(text);
        if (!m.find()) return null;
        String g = m.group(1).toLowerCase(Locale.ROOT);
        if (g.contains("남") || g.equals("男")) return "male";
        if (g.contains("여") || g.equals("女")) return "female";
        return null;
    }
}
