package com.partnerizt.service;

import com.partnerizt.dto.EndSessionRequest;
import com.partnerizt.dto.ExplorationSessionDto;
import com.partnerizt.dto.StartSessionRequest;
import com.partnerizt.dto.UpdateSessionRequest;

import java.util.List;

public interface ExplorationSessionService {
    ExplorationSessionDto startSession(StartSessionRequest request);
    ExplorationSessionDto getActiveSession(Long userId);
    ExplorationSessionDto updateActiveSession(Long sessionId, UpdateSessionRequest request);
    ExplorationSessionDto endSession(Long sessionId, EndSessionRequest request);
    List<ExplorationSessionDto> getUserSessions(Long userId);
}
