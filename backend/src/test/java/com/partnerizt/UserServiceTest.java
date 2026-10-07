package com.partnerizt;

import com.partnerizt.dto.UserDto;
import com.partnerizt.dto.UserStatsDto;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.User;
import com.partnerizt.repository.UserRepository;
import com.partnerizt.service.GamificationService;
import com.partnerizt.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GamificationService gamificationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("nature_scout", "explorer@partnerizt.app");
        testUser.setId(1L);
        testUser.setLevel(1);
        testUser.setXp(50);
        testUser.setCoins(25);
        testUser.setTotalDistance(1000.0);
        testUser.setTotalExplorationTime(600L);
    }

    @Test
    @DisplayName("Should get user by ID")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gamificationService.calculateXpForNextLevel(1)).thenReturn(250);

        UserDto dto = userService.getUserById(1L);

        assertNotNull(dto);
        assertEquals("nature_scout", dto.getUsername());
        assertEquals(1, dto.getLevel());
        assertEquals(50, dto.getXp());
        assertEquals(250, dto.getXpForNextLevel());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent user")
    void testGetMissingUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    @DisplayName("Should add XP/coins, recalculate level, and persist user")
    void testAddXpAndCoins() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gamificationService.calculateLevel(150)).thenReturn(1);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto updated = userService.addXpAndCoins(1L, 100, 20);

        assertNotNull(updated);
        assertEquals(150, updated.getXp());
        assertEquals(45, updated.getCoins());
        verify(gamificationService, times(1)).checkAndAwardBadges(testUser);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should update user exploration stats and trigger badge check")
    void testUpdateExplorationStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateExplorationStats(1L, 500.0, 300L);

        assertEquals(1500.0, testUser.getTotalDistance());
        assertEquals(900L, testUser.getTotalExplorationTime());
        verify(gamificationService, times(1)).checkAndAwardBadges(testUser);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should fetch comprehensive user stats")
    void testGetUserStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gamificationService.calculateXpForNextLevel(1)).thenReturn(250);
        when(gamificationService.getUserBadgesWithStatus(1L)).thenReturn(Collections.emptyList());

        UserStatsDto stats = userService.getUserStats(1L);

        assertNotNull(stats);
        assertEquals(1000.0, stats.getTotalDistance());
        assertEquals(600L, stats.getTotalExplorationTime());
        assertEquals(1, stats.getLevel());
        assertEquals(50, stats.getXp());
    }
}
