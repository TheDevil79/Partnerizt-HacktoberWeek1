package com.partnerizt.service;

import com.partnerizt.dto.UserDto;
import com.partnerizt.dto.UserStatsDto;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.User;
import com.partnerizt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GamificationService gamificationService;

    public UserServiceImpl(UserRepository userRepository, GamificationService gamificationService) {
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
    }

    @Override
    public UserDto getCurrentUser() {
        User user = getOrCreateDefaultUser();
        return mapToDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public User getOrCreateDefaultUser() {
        return userRepository.findByUsername("nature_scout").orElseGet(() -> {
            User newUser = new User("nature_scout", "explorer@partnerizt.app");
            newUser.setLevel(1);
            newUser.setXp(120);
            newUser.setCoins(75);
            newUser.setStreak(3);
            newUser.setTotalDistance(3420.0);
            newUser.setTotalExplorationTime(4800L);
            newUser.setDiscoveriesCount(5);
            newUser.setQuestsCompletedCount(2);
            return userRepository.save(newUser);
        });
    }

    @Override
    public UserStatsDto getUserStats(Long userId) {
        User user = (userId != null) ? userRepository.findById(userId)
                .orElseGet(this::getOrCreateDefaultUser) : getOrCreateDefaultUser();

        UserStatsDto stats = new UserStatsDto();
        stats.setTotalDistance(user.getTotalDistance());
        stats.setTotalExplorationTime(user.getTotalExplorationTime());
        stats.setStreak(user.getStreak());
        stats.setLevel(user.getLevel());
        stats.setXp(user.getXp());
        stats.setXpForNextLevel(gamificationService.calculateXpForNextLevel(user.getLevel()));
        stats.setCoins(user.getCoins());
        stats.setTotalDiscoveries(user.getDiscoveriesCount());
        stats.setTotalQuestsCompleted(user.getQuestsCompletedCount());
        stats.setTodayDistance(850.0); // Sample today's active metric
        stats.setTodayExplorationTime(1500L);
        stats.setBadges(gamificationService.getUserBadgesWithStatus(user.getId()));

        return stats;
    }

    @Override
    @Transactional
    public UserDto addXpAndCoins(Long userId, int xpToAdd, int coinsToAdd) {
        User user = (userId != null) ? userRepository.findById(userId)
                .orElseGet(this::getOrCreateDefaultUser) : getOrCreateDefaultUser();

        int newXp = user.getXp() + xpToAdd;
        int newCoins = user.getCoins() + coinsToAdd;
        int newLevel = gamificationService.calculateLevel(newXp);

        user.setXp(newXp);
        user.setCoins(newCoins);
        user.setLevel(newLevel);
        user.setLastActiveDate(LocalDateTime.now());

        gamificationService.checkAndAwardBadges(user);
        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void updateExplorationStats(Long userId, double distanceMeters, long durationSeconds) {
        User user = (userId != null) ? userRepository.findById(userId)
                .orElseGet(this::getOrCreateDefaultUser) : getOrCreateDefaultUser();

        user.setTotalDistance(user.getTotalDistance() + distanceMeters);
        user.setTotalExplorationTime(user.getTotalExplorationTime() + durationSeconds);
        user.setLastActiveDate(LocalDateTime.now());

        gamificationService.checkAndAwardBadges(user);
        userRepository.save(user);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setLevel(user.getLevel());
        dto.setXp(user.getXp());
        dto.setXpForNextLevel(gamificationService.calculateXpForNextLevel(user.getLevel()));
        dto.setCoins(user.getCoins());
        dto.setStreak(user.getStreak());
        dto.setTotalDistance(user.getTotalDistance());
        dto.setTotalExplorationTime(user.getTotalExplorationTime());
        dto.setDiscoveriesCount(user.getDiscoveriesCount());
        dto.setQuestsCompletedCount(user.getQuestsCompletedCount());
        dto.setLastActiveDate(user.getLastActiveDate());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
