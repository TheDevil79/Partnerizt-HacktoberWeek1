package com.partnerizt.service;

import com.partnerizt.dto.BadgeDto;
import com.partnerizt.model.Badge;
import com.partnerizt.model.User;
import com.partnerizt.model.UserBadge;
import com.partnerizt.repository.BadgeRepository;
import com.partnerizt.repository.UserBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GamificationServiceImpl implements GamificationService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public GamificationServiceImpl(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public int calculateXpForNextLevel(int currentLevel) {
        // Progressive formula: Level 1 = 200 XP, Level 2 = 500 XP, Level 3 = 900 XP, etc.
        return currentLevel * 250 + (int) (Math.pow(currentLevel, 1.5) * 50);
    }

    @Override
    public int calculateLevel(int currentXp) {
        int level = 1;
        while (currentXp >= calculateXpForNextLevel(level)) {
            level++;
        }
        return level;
    }

    @Override
    public List<BadgeDto> getUserBadgesWithStatus(Long userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        Map<String, UserBadge> userBadgeMap = userBadgeRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(UserBadge::getBadgeId, ub -> ub, (a, b) -> a));

        return allBadges.stream().map(badge -> {
            UserBadge ub = userBadgeMap.get(badge.getId());
            boolean unlocked = ub != null;
            LocalDateTime unlockedAt = ub != null ? ub.getUnlockedAt() : null;
            return new BadgeDto(
                    badge.getId(),
                    badge.getTitle(),
                    badge.getDescription(),
                    badge.getIconName(),
                    badge.getCategory(),
                    badge.getRequiredValue(),
                    badge.getXpBonus(),
                    unlocked,
                    unlockedAt
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkAndAwardBadges(User user) {
        if (user == null || user.getId() == null) return;

        List<Badge> allBadges = badgeRepository.findAll();
        Set<String> alreadyUnlocked = userBadgeRepository.findByUserId(user.getId()).stream()
                .map(UserBadge::getBadgeId)
                .collect(Collectors.toSet());

        for (Badge badge : allBadges) {
            if (alreadyUnlocked.contains(badge.getId())) {
                continue;
            }

            boolean qualifies = false;
            switch (badge.getCategory()) {
                case "DISTANCE":
                    qualifies = user.getTotalDistance() >= badge.getRequiredValue();
                    break;
                case "DISCOVERY":
                    qualifies = user.getDiscoveriesCount() >= badge.getRequiredValue();
                    break;
                case "STREAK":
                    qualifies = user.getStreak() >= badge.getRequiredValue();
                    break;
                case "QUESTS":
                    qualifies = user.getQuestsCompletedCount() >= badge.getRequiredValue();
                    break;
                default:
                    break;
            }

            if (qualifies) {
                UserBadge newBadge = new UserBadge(user.getId(), badge.getId());
                userBadgeRepository.save(newBadge);
                user.setXp(user.getXp() + badge.getXpBonus());
            }
        }
    }
}
