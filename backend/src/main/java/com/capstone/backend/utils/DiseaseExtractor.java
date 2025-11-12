// com.capstone.backend.ocr.DiseaseExtractor.java
package com.capstone.backend.ocr;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class DiseaseExtractor {

    // ===== 키워드 기반(직접 명시) =====
    private static final String[] DIRECT_KEYWORDS = {
        "당뇨", "당뇨병", "고혈압", "이상지질혈증", "고지혈증",
        "심부전", "심부전증", "협심증", "심근경색", "뇌졸중", "뇌출혈",
        "천식", "COPD", "만성 폐쇄성 폐질환", "간경변", "간경변증",
        "갑상선 기능저하증", "갑상선 기능항진증", "골다공증", "비만", "과체중"
    };

    // ===== 수치 패턴(추론용, 저장은 하지 않음) =====
    private static final Pattern HBA1C = Pattern.compile("HbA1c\\s*(\\d{1,2}(?:[.,]\\d)?)");
    private static final Pattern BP    = Pattern.compile("혈압\\s*\\(mmHg\\)\\s*(\\d{2,3})\\s*/\\s*(\\d{2,3})");
    private static final Pattern LDL   = Pattern.compile("LDL\\s*콜레스테롤\\s*(\\d{2,3})\\b");
    private static final Pattern HDL   = Pattern.compile("HDL\\s*콜레스테롤\\s*(\\d{2,3})\\b");
    private static final Pattern CHOL  = Pattern.compile("총콜레스테롤\\s*(\\d{2,3})\\b");
    private static final Pattern BMI   = Pattern.compile("BMI\\s*(\\d{1,2}(?:[.,]\\d)?)");
    private static final Pattern JUDG_OVER = Pattern.compile("판정\\s*[:：]?\\s*(과체중|비만|경계)"); // 표의 '판정' 칼럼 텍스트도 잡기

    private static Double toDouble(String s) {
        try { return Double.valueOf(s.replace(',', '.')); } catch (Exception e) { return null; }
    }
    private static Integer toInt(String s) {
        try { return Integer.valueOf(s); } catch (Exception e) { return null; }
    }

    public static List<String> extractDiseases(String raw) {
        if (raw == null) return List.of();

        String text = raw.replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);

        // 1) 직접 명시된 질병
        Set<String> out = new LinkedHashSet<>();
        for (String kw : DIRECT_KEYWORDS) {
            if (text.contains(kw.toLowerCase(Locale.ROOT))) {
                out.add(canon(kw));
            }
        }

        // 2) 수치 기반 규칙(판정 텍스트 포함) → "질병명"만 추가, 수치는 저장하지 않음
        // 2-1) 당뇨(= HbA1c >= 6.5 또는 '당뇨' 관련 문구)
        Matcher mH = HBA1C.matcher(text);
        if (mH.find()) {
            Double v = toDouble(mH.group(1));
            if (v != null && v >= 6.5) out.add("당뇨병");
        }

        // 2-2) 고혈압(= 수축기 >=140 또는 이완기 >=90, 혹은 '혈압 이상' 문구)
        Matcher mB = BP.matcher(text);
        if (mB.find()) {
            Integer sys = toInt(mB.group(1));
            Integer dia = toInt(mB.group(2));
            if ((sys != null && sys >= 140) || (dia != null && dia >= 90)) out.add("고혈압");
        }
        if (text.contains("혈압") && text.contains("이상")) out.add("고혈압"); // 표의 판정 칼럼 보조

        // 2-3) 이상지질혈증(= LDL>=130 또는 총콜>=200 또는 HDL<40)
        Matcher mLDL = LDL.matcher(text);
        if (mLDL.find()) {
            Integer v = toInt(mLDL.group(1));
            if (v != null && v >= 130) out.add("이상지질혈증");
        }
        Matcher mCH = CHOL.matcher(text);
        if (mCH.find()) {
            Integer v = toInt(mCH.group(1));
            if (v != null && v >= 200) out.add("이상지질혈증");
        }
        Matcher mHDL = HDL.matcher(text);
        if (mHDL.find()) {
            Integer v = toInt(mHDL.group(1));
            if (v != null && v < 40) out.add("이상지질혈증");
        }

        // 2-4) 과체중/비만(BMI 기준 or 판정 텍스트)
        Matcher mBMI = BMI.matcher(text);
        if (mBMI.find()) {
            Double v = toDouble(mBMI.group(1));
            if (v != null) {
                if (v >= 25.0) out.add("비만");
                else if (v >= 23.0) out.add("과체중");
            }
        }
        Matcher mJudg = JUDG_OVER.matcher(text);
        if (mJudg.find()) {
            String j = mJudg.group(1);
            if ("과체중".equals(j)) out.add("과체중");
            if ("비만".equals(j)) out.add("비만");
        }

        // 2-5) 종합 판정 문장(예: "당뇨병 및 심부전 의심")
        if (text.contains("종합 판정") || text.contains("종합판정")) {
            // 간단 키워드 매칭
            if (text.contains("심부전")) out.add("심부전");
            if (text.contains("당뇨")) out.add("당뇨병");
        }

        return new ArrayList<>(out);
    }

    // 표준명 매핑(동의어 → 표준 라벨)
    private static String canon(String s) {
        s = s.replace("고지혈증", "이상지질혈증");
        s = s.replace("당뇨", "당뇨병");
        return s;
    }
}
