package com.partnerizt.service;

import com.partnerizt.dto.BadgeDto;
import com.partnerizt.model.User;

import java.util.List;

public interface GamificationService {
    int calculateXpForNextLevel(int currentLevel);
    int calculateLevel(int currentXp);
    List<BadgeDto> getUserBadgesWithStatus(Long userId);
    void checkAndAwardBadges(User user);
}
