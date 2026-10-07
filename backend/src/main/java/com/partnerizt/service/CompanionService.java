package com.partnerizt.service;

import com.partnerizt.dto.CompanionDto;
import com.partnerizt.model.Companion;

import java.util.List;

public interface CompanionService {
    List<CompanionDto> getAllCompanions();
    CompanionDto getCompanionById(String id);
    Companion getCompanionEntityById(String id);
}
