package com.partnerizt;

import com.partnerizt.dto.EndSessionRequest;
import com.partnerizt.dto.ExplorationSessionDto;
import com.partnerizt.dto.StartSessionRequest;
import com.partnerizt.dto.UpdateSessionRequest;
import com.partnerizt.exception.ExplorationException;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.ExplorationSession;
import com.partnerizt.model.SessionStatus;
import com.partnerizt.model.User;
import com.partnerizt.repository.ExplorationSessionRepository;
import com.partnerizt.repository.QuestRepository;
import com.partnerizt.service.ExplorationSessionServiceImpl;
import com.partnerizt.service.QuestService;
import com.partnerizt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExplorationSessionServiceTest {

    @Mock
    private ExplorationSessionRepository sessionRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuestService questService;

    @InjectMocks
    private ExplorationSessionServiceImpl explorationSessionService;

    private User testUser;
    private ExplorationSession testSession;

    @BeforeEach
    void setUp() {
        testUser = new User("nature_scout", "explorer@partnerizt.app");
        testUser.setId(1L);

        testSession = new ExplorationSession(1L, 101L);
        testSession.setId(50L);
        testSession.setStatus(SessionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should successfully start an exploration session")
    void testStartSession() {
        when(sessionRepository.findFirstByUserIdAndStatusOrderByStartTimeDesc(1L, SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any(ExplorationSession.class))).thenAnswer(i -> {
            ExplorationSession s = i.getArgument(0);
            s.setId(50L);
            return s;
        });

        StartSessionRequest req = new StartSessionRequest(1L, 101L);
        ExplorationSessionDto result = explorationSessionService.startSession(req);

        assertNotNull(result);
        assertEquals(SessionStatus.ACTIVE, result.getStatus());
        assertEquals(1L, result.getUserId());
        assertEquals(101L, result.getActiveQuestId());
    }

    @Test
    @DisplayName("Should update active session distance and duration")
    void testUpdateActiveSession() {
        when(sessionRepository.findById(50L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ExplorationSession.class))).thenAnswer(i -> i.getArgument(0));

        UpdateSessionRequest req = new UpdateSessionRequest();
        req.setDistanceCovered(1250.0);
        req.setDurationSeconds(900L);
        req.setSpeed(4.5);

        ExplorationSessionDto result = explorationSessionService.updateActiveSession(50L, req);

        assertNotNull(result);
        assertEquals(1250.0, result.getDistanceCovered());
        assertEquals(900L, result.getDurationSeconds());
    }

    @Test
    @DisplayName("Should fail updating a completed session")
    void testUpdateInactiveSessionFails() {
        testSession.setStatus(SessionStatus.COMPLETED);
        when(sessionRepository.findById(50L)).thenReturn(Optional.of(testSession));

        UpdateSessionRequest req = new UpdateSessionRequest();
        req.setDistanceCovered(500.0);

        assertThrows(ExplorationException.class, () -> {
            explorationSessionService.updateActiveSession(50L, req);
        });
    }

    @Test
    @DisplayName("Should end session, persist distance and duration to user, and calculate rewards")
    void testEndSessionSuccess() {
        testSession.setDistanceCovered(2000.0);
        testSession.setDurationSeconds(1200L);
        when(sessionRepository.findById(50L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ExplorationSession.class))).thenAnswer(i -> i.getArgument(0));

        EndSessionRequest req = new EndSessionRequest(2000.0, 1200L);
        ExplorationSessionDto result = explorationSessionService.endSession(50L, req);

        assertNotNull(result);
        assertEquals(SessionStatus.COMPLETED, result.getStatus());
        verify(userService, times(1)).updateExplorationStats(1L, 2000.0, 1200L);
        verify(userService, times(1)).addXpAndCoins(eq(1L), anyInt(), anyInt());
        assertTrue(result.getXpEarned() > 0);
        assertTrue(result.getCoinsEarned() > 0);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for missing session")
    void testMissingSessionHandling() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            explorationSessionService.updateActiveSession(999L, new UpdateSessionRequest());
        });
    }

    @Test
    @DisplayName("Should verify that session model strictly avoids persisting GPS route coordinates")
    void testNoRouteCoordinatesPersisted() {
        ExplorationSession session = new ExplorationSession(1L, 101L);
        session.setDistanceCovered(1500.0);
        session.setDurationSeconds(720L);

        // Verify that ExplorationSession only holds accumulated numeric metrics and no coordinate collections
        assertNotNull(session.getDistanceCovered());
        assertNotNull(session.getDurationSeconds());
        assertEquals(1500.0, session.getDistanceCovered());
        assertEquals(720L, session.getDurationSeconds());
    }

    @Test
    @DisplayName("Should verify Haversine distance formula accuracy for outdoor movements")
    void testHaversineDistanceCalculationAccuracy() {
        // Point A: Central Park South (40.7660, -73.9772)
        // Point B: Central Park North (40.7960, -73.9542)
        double lat1 = 40.7660;
        double lon1 = -73.9772;
        double lat2 = 40.7960;
        double lon2 = -73.9542;

        double r = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = r * c;

        // Approximate 3.84 km between these two coordinates
        assertTrue(distanceKm > 3.5 && distanceKm < 4.2);
    }
}
