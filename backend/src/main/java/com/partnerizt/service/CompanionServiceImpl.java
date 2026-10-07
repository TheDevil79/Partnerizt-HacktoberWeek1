package com.partnerizt.service;

import com.partnerizt.dto.CompanionDto;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.Companion;
import com.partnerizt.repository.CompanionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanionServiceImpl implements CompanionService {

    private final CompanionRepository companionRepository;

    public CompanionServiceImpl(CompanionRepository companionRepository) {
        this.companionRepository = companionRepository;
    }

    @Override
    public List<CompanionDto> getAllCompanions() {
        return companionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompanionDto getCompanionById(String id) {
        Companion companion = getCompanionEntityById(id);
        return mapToDto(companion);
    }

    @Override
    public Companion getCompanionEntityById(String id) {
        return companionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Companion", "id", id));
    }

    public CompanionDto mapToDto(Companion c) {
        CompanionDto dto = new CompanionDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setTitle(c.getTitle());
        dto.setDomain(c.getDomain());
        dto.setExpertise(c.getExpertise());
        dto.setDescription(c.getDescription());
        dto.setAvatarType(c.getAvatarType());
        dto.setAvatarColor(c.getAvatarColor());
        dto.setPersonality(c.getPersonality());
        dto.setSampleOutdoorActivities(c.getSampleOutdoorActivities());
        dto.setPromptStarters(c.getPromptStarters());
        return dto;
    }
}
