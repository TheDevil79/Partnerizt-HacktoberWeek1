package com.partnerizt.service;

import com.partnerizt.dto.CreateDiscoveryRequest;
import com.partnerizt.dto.DiscoveryDto;
import com.partnerizt.dto.IdentifyPhotoRequest;
import com.partnerizt.dto.IdentifyPhotoResponse;

import java.util.List;

public interface DiscoveryService {
    List<DiscoveryDto> getUserDiscoveries(Long userId);
    DiscoveryDto getDiscoveryById(Long id);
    DiscoveryDto createDiscovery(CreateDiscoveryRequest request);
    IdentifyPhotoResponse identifyPhoto(IdentifyPhotoRequest request);
}
