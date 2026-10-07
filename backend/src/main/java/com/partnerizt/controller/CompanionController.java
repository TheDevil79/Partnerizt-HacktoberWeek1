package com.partnerizt.controller;

import com.partnerizt.dto.ApiResponse;
import com.partnerizt.dto.CompanionDto;
import com.partnerizt.service.CompanionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companions")
public class CompanionController {

    private final CompanionService companionService;

    public CompanionController(CompanionService companionService) {
        this.companionService = companionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanionDto>>> getAllCompanions() {
        List<CompanionDto> companions = companionService.getAllCompanions();
        return ResponseEntity.ok(ApiResponse.success(companions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanionDto>> getCompanionById(@PathVariable String id) {
        CompanionDto companion = companionService.getCompanionById(id);
        return ResponseEntity.ok(ApiResponse.success(companion));
    }
}
