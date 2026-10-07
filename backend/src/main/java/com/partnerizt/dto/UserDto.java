package com.partnerizt.dto;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Integer level;
    private Integer xp;
    private Integer xpForNextLevel;
    private Integer coins;
    private Integer streak;
    private Double totalDistance;
    private Long totalExplorationTime;
    private Integer discoveriesCount;
    private Integer questsCompletedCount;
    private LocalDateTime lastActiveDate;
    private LocalDateTime createdAt;

    public UserDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getXpForNextLevel() {
        return xpForNextLevel;
    }

    public void setXpForNextLevel(Integer xpForNextLevel) {
        this.xpForNextLevel = xpForNextLevel;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Long getTotalExplorationTime() {
        return totalExplorationTime;
    }

    public void setTotalExplorationTime(Long totalExplorationTime) {
        this.totalExplorationTime = totalExplorationTime;
    }

    public Integer getDiscoveriesCount() {
        return discoveriesCount;
    }

    public void setDiscoveriesCount(Integer discoveriesCount) {
        this.discoveriesCount = discoveriesCount;
    }

    public Integer getQuestsCompletedCount() {
        return questsCompletedCount;
    }

    public void setQuestsCompletedCount(Integer questsCompletedCount) {
        this.questsCompletedCount = questsCompletedCount;
    }

    public LocalDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDateTime lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
