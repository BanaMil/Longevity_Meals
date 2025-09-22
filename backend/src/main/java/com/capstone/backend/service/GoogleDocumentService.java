package com.capstone.backend.service;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GoogleDocumentService {

    private final DocumentProcessorServiceClient client;

    @Value("${google.document.project-id}")
    private String projectId;

    @Value("${google.document.location}")
    private String location;

    @Value("${google.document.processor-id}")
    private String processorId;

    public GoogleDocumentService(DocumentProcessorServiceClient client) {
        this.client = client;
    }

    public String extractTextFromImage(File imageFile) throws IOException {
        String name = String.format("projects/%s/locations/%s/processors/%s", 
                                   projectId, location, processorId);

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        ByteString content = ByteString.copyFrom(imageBytes);

        // MIME 타입을 파일 확장자에 따라 동적으로 설정
        String mimeType = getMimeType(imageFile.getName());

        RawDocument document = RawDocument.newBuilder()
            .setContent(content)
            .setMimeType(mimeType)
            .build();

        ProcessRequest request = ProcessRequest.newBuilder()
            .setName(name)
            .setRawDocument(document)
            .build();

        ProcessResponse response = client.processDocument(request);
        Document resultDocument = response.getDocument();

        log.info("[Google Document AI] 텍스트 추출 완료: {} characters", 
                resultDocument.getText().length());
        return resultDocument.getText();
    }

    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "pdf" -> "application/pdf";
            default -> "image/png";
        };
    }

    public List<String> extractDiseases(String extractedText) {
        List<String> diseases = new ArrayList<>();
        
        // 건강검진 결과서에서 질병명을 추출하는 패턴들
        String[] diseasePatterns = {
            "당뇨병", "고혈압", "고지혈증", "비만", "골다공증", "간질환", "신장질환",
            "심장질환", "뇌혈관질환", "갑상선질환", "빈혈", "위염", "위궤양", "십이지장궤양",
            "대장용종", "유방암", "자궁근종", "전립선비대증", "백내장", "녹내장"
        };

        for (String disease : diseasePatterns) {
            if (extractedText.contains(disease)) {
                diseases.add(disease);
                log.info("[질병 추출] 발견된 질병: {}", disease);
            }
        }

        // 수치 기반 질병 판단 (예: 혈당, 혈압 등)
        diseases.addAll(extractDiseasesFromValues(extractedText));

        return diseases;
    }

    private List<String> extractDiseasesFromValues(String text) {
        List<String> diseases = new ArrayList<>();

        // 혈당 수치로 당뇨병 판단
        Pattern glucosePattern = Pattern.compile("공복혈당.*?(\\d+)");
        Matcher glucoseMatcher = glucosePattern.matcher(text);
        if (glucoseMatcher.find()) {
            int glucose = Integer.parseInt(glucoseMatcher.group(1));
            if (glucose >= 126) {
                diseases.add("당뇨병");
                log.info("[수치 기반 질병 판단] 공복혈당 {}로 당뇨병 추가", glucose);
            }
        }

        // 혈압 수치로 고혈압 판단
        Pattern bpPattern = Pattern.compile("혈압.*?(\\d+)/(\\d+)");
        Matcher bpMatcher = bpPattern.matcher(text);
        if (bpMatcher.find()) {
            int systolic = Integer.parseInt(bpMatcher.group(1));
            int diastolic = Integer.parseInt(bpMatcher.group(2));
            if (systolic >= 140 || diastolic >= 90) {
                diseases.add("고혈압");
                log.info("[수치 기반 질병 판단] 혈압 {}/{}로 고혈압 추가", systolic, diastolic);
            }
        }

        return diseases;
    }
}
