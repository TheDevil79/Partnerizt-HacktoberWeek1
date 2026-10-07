package com.partnerizt.service;

import com.partnerizt.dto.UpdateUserRequest;
import com.partnerizt.dto.UserDto;
import com.partnerizt.dto.UserStatsDto;
import com.partnerizt.model.User;

public interface UserService {
    UserDto getCurrentUser();
    UserDto getUserById(Long userId);
    User getOrCreateDefaultUser();
    UserStatsDto getUserStats(Long userId);
    UserDto addXpAndCoins(Long userId, int xpToAdd, int coinsToAdd);
    void updateExplorationStats(Long userId, double distanceMeters, long durationSeconds);
}
