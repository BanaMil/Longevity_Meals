package com.capstone.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "disease_keywords")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseKeywordMapping {
    private String keyword;      // 예: "고혈압", "혈압 140", "수축기 150"
    private String diseaseId;    // 예: "HTN"
}
