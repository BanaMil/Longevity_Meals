package com.capstone.backend.service;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.dto.HealthInfoRequest;
import com.capstone.backend.repository.HealthInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthInfoService {

    private final HealthInfoRepository healthInfoRepository;

    public void saveHealthInfo(HealthInfoRequest request) {
        HealthInfo healthInfo = HealthInfo.builder()
                .height(request.getHeight())
                .weight(request.getWeight())
                .diseases(request.getDiseases())
                .allergies(request.getAllergies())
                .dislikes(request.getDislikes())
                .build();

        healthInfoRepository.save(healthInfo);
    }
}
