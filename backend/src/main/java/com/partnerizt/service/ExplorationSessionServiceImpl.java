package com.partnerizt.service;

import com.partnerizt.dto.EndSessionRequest;
import com.partnerizt.dto.ExplorationSessionDto;
import com.partnerizt.dto.QuestDto;
import com.partnerizt.dto.StartSessionRequest;
import com.partnerizt.dto.UpdateSessionRequest;
import com.partnerizt.exception.ExplorationException;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.ExplorationSession;
import com.partnerizt.model.SessionStatus;
import com.partnerizt.repository.ExplorationSessionRepository;
import com.partnerizt.repository.QuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExplorationSessionServiceImpl implements ExplorationSessionService {

    private final ExplorationSessionRepository sessionRepository;
    private final QuestRepository questRepository;
    private final UserService userService;
    private final QuestService questService;

    public ExplorationSessionServiceImpl(ExplorationSessionRepository sessionRepository,
                                         QuestRepository questRepository,
                                         UserService userService,
                                         QuestService questService) {
        this.sessionRepository = sessionRepository;
        this.questRepository = questRepository;
        this.userService = userService;
        this.questService = questService;
    }

    @Override
    @Transactional
    public ExplorationSessionDto startSession(StartSessionRequest request) {
        Long userId = (request != null && request.getUserId() != null)
                ? request.getUserId()
                : userService.getOrCreateDefaultUser().getId();

        // If there's already an active session, return it
        Optional<ExplorationSession> existing = sessionRepository
                .findFirstByUserIdAndStatusOrderByStartTimeDesc(userId, SessionStatus.ACTIVE);
        if (existing.isPresent()) {
            return mapToDto(existing.get());
        }

        Long activeQuestId = (request != null) ? request.getActiveQuestId() : null;
        ExplorationSession session = new ExplorationSession(userId, activeQuestId);
        ExplorationSession saved = sessionRepository.save(session);
        return mapToDto(saved);
    }

    @Override
    public ExplorationSessionDto getActiveSession(Long userId) {
        Long resolvedUserId = (userId != null) ? userId : userService.getOrCreateDefaultUser().getId();
        return sessionRepository.findFirstByUserIdAndStatusOrderByStartTimeDesc(resolvedUserId, SessionStatus.ACTIVE)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public ExplorationSessionDto updateActiveSession(Long sessionId, UpdateSessionRequest request) {
        ExplorationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("ExplorationSession", "id", sessionId));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ExplorationException("Cannot update an inactive session");
        }

        if (request.getDistanceCovered() != null) {
            session.setDistanceCovered(request.getDistanceCovered());
        }
        if (request.getDurationSeconds() != null) {
            session.setDurationSeconds(request.getDurationSeconds());
        }
        if (request.getSpeed() != null) {
            session.setAverageSpeed(request.getSpeed());
        }

        ExplorationSession saved = sessionRepository.save(session);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ExplorationSessionDto endSession(Long sessionId, EndSessionRequest request) {
        ExplorationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("ExplorationSession", "id", sessionId));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            return mapToDto(session);
        }

        double finalDist = (request != null && request.getFinalDistance() != null)
                ? request.getFinalDistance()
                : session.getDistanceCovered();
        long finalDuration = (request != null && request.getFinalDurationSeconds() != null)
                ? request.getFinalDurationSeconds()
                : session.getDurationSeconds();

        session.setDistanceCovered(finalDist);
        session.setDurationSeconds(finalDuration);
        session.setEndTime(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);

        // Calculate XP and Coin rewards for physical exploration:
        // 10 XP per 100 meters + 5 XP per minute explored + 20 XP per discovery
        int xpEarned = (int) (finalDist / 10.0) + (int) (finalDuration / 12.0) + (session.getDiscoveriesCount() * 20);
        int coinsEarned = (int) (finalDist / 100.0) + (int) (finalDuration / 60.0);

        // Persist distance and time to user
        userService.updateExplorationStats(session.getUserId(), finalDist, finalDuration);
        userService.addXpAndCoins(session.getUserId(), Math.max(20, xpEarned), Math.max(5, coinsEarned));

        ExplorationSession saved = sessionRepository.save(session);
        ExplorationSessionDto dto = mapToDto(saved);
        dto.setXpEarned(Math.max(20, xpEarned));
        dto.setCoinsEarned(Math.max(5, coinsEarned));
        return dto;
    }

    @Override
    public List<ExplorationSessionDto> getUserSessions(Long userId) {
        Long resolvedUserId = (userId != null) ? userId : userService.getOrCreateDefaultUser().getId();
        return sessionRepository.findByUserIdOrderByStartTimeDesc(resolvedUserId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ExplorationSessionDto mapToDto(ExplorationSession s) {
        ExplorationSessionDto dto = new ExplorationSessionDto();
        dto.setId(s.getId());
        dto.setUserId(s.getUserId());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setDistanceCovered(s.getDistanceCovered());
        dto.setDurationSeconds(s.getDurationSeconds());
        dto.setActiveQuestId(s.getActiveQuestId());
        dto.setStatus(s.getStatus());
        dto.setDiscoveriesCount(s.getDiscoveriesCount());
        dto.setAverageSpeed(s.getAverageSpeed());

        if (s.getActiveQuestId() != null) {
            questRepository.findById(s.getActiveQuestId()).ifPresent(q -> {
                QuestDto questDto = questService.getQuestById(q.getId());
                dto.setActiveQuest(questDto);
            });
        }

        return dto;
    }
}
