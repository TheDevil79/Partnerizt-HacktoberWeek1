package com.partnerizt;

import com.partnerizt.dto.BadgeDto;
import com.partnerizt.model.Badge;
import com.partnerizt.model.User;
import com.partnerizt.model.UserBadge;
import com.partnerizt.repository.BadgeRepository;
import com.partnerizt.repository.UserBadgeRepository;
import com.partnerizt.service.GamificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private GamificationServiceImpl gamificationService;

    private Badge distanceBadge;
    private Badge questBadge;

    @BeforeEach
    void setUp() {
        distanceBadge = new Badge("first_step", "First Footprint", "100m walked", "footprints", "DISTANCE", 100.0, 50);
        questBadge = new Badge("quest_master", "Quest Master", "5 quests completed", "trophy", "QUESTS", 5.0, 180);
    }

    @Test
    @DisplayName("Should correctly calculate level from XP")
    void testCalculateLevel() {
        assertEquals(1, gamificationService.calculateLevel(50));
        assertEquals(1, gamificationService.calculateLevel(290));
        assertTrue(gamificationService.calculateLevel(1000) >= 2);
    }

    @Test
    @DisplayName("Should check and award qualifying badges to user")
    void testCheckAndAwardBadges() {
        User user = new User("explorer", "explorer@test.com");
        user.setId(1L);
        user.setTotalDistance(500.0); // qualifies for distanceBadge (>= 100.0)
        user.setQuestsCompletedCount(1); // does not qualify for questBadge (needs 5)
        user.setXp(100);

        when(badgeRepository.findAll()).thenReturn(List.of(distanceBadge, questBadge));
        when(userBadgeRepository.findByUserId(1L)).thenReturn(List.of()); // No badges yet

        gamificationService.checkAndAwardBadges(user);

        verify(userBadgeRepository, times(1)).save(any(UserBadge.class));
        assertEquals(150, user.getXp()); // 100 + 50 bonus
    }

    @Test
    @DisplayName("Should retrieve badges with unlocked status for user")
    void testGetUserBadgesWithStatus() {
        UserBadge unlocked = new UserBadge(1L, "first_step");

        when(badgeRepository.findAll()).thenReturn(List.of(distanceBadge, questBadge));
        when(userBadgeRepository.findByUserId(1L)).thenReturn(List.of(unlocked));

        List<BadgeDto> badges = gamificationService.getUserBadgesWithStatus(1L);

        assertEquals(2, badges.size());
        assertTrue(badges.stream().anyMatch(b -> b.getId().equals("first_step") && b.isUnlocked()));
        assertTrue(badges.stream().anyMatch(b -> b.getId().equals("quest_master") && !b.isUnlocked()));
    }
}
