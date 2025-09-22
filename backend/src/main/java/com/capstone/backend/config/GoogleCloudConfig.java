package com.capstone.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

    @Value("${google.cloud.credentials.path:}")
    private String credentialsPath;

    @Bean
    public DocumentProcessorServiceClient documentProcessorServiceClient() throws IOException {
        DocumentProcessorServiceSettings.Builder settingsBuilder = 
            DocumentProcessorServiceSettings.newBuilder();

        if (!credentialsPath.isEmpty()) {
            GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath));
            settingsBuilder.setCredentialsProvider(() -> credentials);
        }

        return DocumentProcessorServiceClient.create(settingsBuilder.build());
    }
}
