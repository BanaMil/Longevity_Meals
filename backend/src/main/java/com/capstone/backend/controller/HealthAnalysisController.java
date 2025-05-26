package com.capstone.backend.controller;

import com.capstone.backend.domain.HealthInfo;
import com.capstone.backend.domain.NutrientReference;
import com.capstone.backend.dto.HealthAnalysisResponse;
import com.capstone.backend.mapper.HealthAnalysisMapper;
import com.capstone.backend.repository.HealthInfoRepository;
import com.capstone.backend.repository.NutrientReferenceRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthAnalysisController {

    private final HealthInfoRepository healthInfoRepository;
    private final NutrientReferenceRepository referenceRepo;
    private final HealthAnalysisMapper healthAnalysisMapper;

    // @GetMapping("/analysis/{userid}")
    // public ResponseEntity<HealthAnalysisResponse> getAnalysis(@PathVariable String userid) {
    //     HealthInfo info = healthInfoRepository.findByUserid(userid)
    //             .orElseThrow(() -> new IllegalArgumentException("사용자 건강 정보 없음: " + userid));

    //     Map<String, NutrientReference> referenceMap = referenceRepo.findAll().stream()
    //             .collect(Collectors.toMap(NutrientReference::getNutrient, r -> r));

    //     HealthAnalysisResponse response = healthAnalysisMapper.buildHealthAnalysisResponse(info, referenceMap);

    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/analysis/{userid}")
    public ResponseEntity<HealthAnalysisResponse> getHealthAnalysis(@PathVariable String userid) {
        HealthInfo info = healthInfoRepository.findByUserid(userid)
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 건강 정보 없음: " + userid)
                );

        Map<String, NutrientReference> referenceMap = referenceRepo.findAll().stream()
            .collect(Collectors.toMap(NutrientReference::getNutrient, r -> r));

        HealthAnalysisResponse response = healthAnalysisMapper.buildHealthAnalysisResponse(info, referenceMap);
        return ResponseEntity.ok(response);
    }
}
