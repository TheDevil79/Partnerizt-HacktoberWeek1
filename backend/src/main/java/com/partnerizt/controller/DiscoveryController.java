package com.partnerizt.controller;

import com.partnerizt.dto.ApiResponse;
import com.partnerizt.dto.CreateDiscoveryRequest;
import com.partnerizt.dto.DiscoveryDto;
import com.partnerizt.dto.IdentifyPhotoRequest;
import com.partnerizt.dto.IdentifyPhotoResponse;
import com.partnerizt.service.DiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discoveries")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscoveryDto>>> getUserDiscoveries(@RequestParam(required = false) Long userId) {
        List<DiscoveryDto> discoveries = discoveryService.getUserDiscoveries(userId);
        return ResponseEntity.ok(ApiResponse.success(discoveries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscoveryDto>> getDiscoveryById(@PathVariable Long id) {
        DiscoveryDto discovery = discoveryService.getDiscoveryById(id);
        return ResponseEntity.ok(ApiResponse.success(discovery));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DiscoveryDto>> createDiscovery(@RequestBody CreateDiscoveryRequest request) {
        DiscoveryDto discovery = discoveryService.createDiscovery(request);
        return ResponseEntity.ok(ApiResponse.success("Discovery catalogued in your Field Journal! XP awarded.", discovery));
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DiscoveryController.class);

    @PostMapping("/identify")
    public ResponseEntity<ApiResponse<IdentifyPhotoResponse>> identifyPhoto(@RequestBody IdentifyPhotoRequest request) {
        log.info("[IDENTIFY_CONTROLLER] request reached backend");
        String reqId = (request != null && request.getRequestId() != null) ? request.getRequestId() : "req_init";
        log.info("[IDENTIFY_REQUEST] requestId={} endpoint=/api/v1/discoveries/identify", reqId);
        IdentifyPhotoResponse identification = discoveryService.identifyPhoto(request);
        return ResponseEntity.ok(ApiResponse.success("Specimen analyzed and identified by domain expert", identification));
    }
}
