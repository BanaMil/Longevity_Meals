package com.capstone.backend.service;

import com.capstone.backend.domain.Disease;
import com.capstone.backend.repository.DiseaseRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.regex.*;
@Service
@RequiredArgsConstructor
public class DiseaseService {
    private final DiseaseRepository diseaseRepository;

    public Optional<String> findDiseaseIdByNameOrAlias(String keyword) {
        return diseaseRepository.findByName(keyword)
                .map(Disease::getId)
                .or(() -> diseaseRepository.findByAliasesContaining(keyword).stream().findFirst().map(Disease::getId));
    }
}
