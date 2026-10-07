package com.partnerizt.controller;

import com.partnerizt.dto.ApiResponse;
import com.partnerizt.dto.EndSessionRequest;
import com.partnerizt.dto.ExplorationSessionDto;
import com.partnerizt.dto.StartSessionRequest;
import com.partnerizt.dto.UpdateSessionRequest;
import com.partnerizt.service.ExplorationSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exploration")
public class ExplorationController {

    private final ExplorationSessionService explorationSessionService;

    public ExplorationController(ExplorationSessionService explorationSessionService) {
        this.explorationSessionService = explorationSessionService;
    }

    @PostMapping("/sessions/start")
    public ResponseEntity<ApiResponse<ExplorationSessionDto>> startSession(@RequestBody(required = false) StartSessionRequest request) {
        ExplorationSessionDto session = explorationSessionService.startSession(request);
        return ResponseEntity.ok(ApiResponse.success("Exploration session started! GPS tracking active.", session));
    }

    @GetMapping("/sessions/active")
    public ResponseEntity<ApiResponse<ExplorationSessionDto>> getActiveSession(@RequestParam(required = false) Long userId) {
        ExplorationSessionDto session = explorationSessionService.getActiveSession(userId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @PutMapping("/sessions/{id}/update")
    public ResponseEntity<ApiResponse<ExplorationSessionDto>> updateActiveSession(
            @PathVariable Long id,
            @RequestBody UpdateSessionRequest request
    ) {
        ExplorationSessionDto session = explorationSessionService.updateActiveSession(id, request);
        return ResponseEntity.ok(ApiResponse.success("Session progress synchronized", session));
    }

    @PostMapping("/sessions/{id}/complete")
    public ResponseEntity<ApiResponse<ExplorationSessionDto>> completeSession(
            @PathVariable Long id,
            @RequestBody(required = false) EndSessionRequest request
    ) {
        ExplorationSessionDto session = explorationSessionService.endSession(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exploration session completed! Distance and time logged.", session));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<ExplorationSessionDto>>> getUserSessions(@RequestParam(required = false) Long userId) {
        List<ExplorationSessionDto> sessions = explorationSessionService.getUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
}
