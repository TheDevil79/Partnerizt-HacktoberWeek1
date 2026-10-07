package com.partnerizt.dto;

import java.util.List;

public class UserStatsDto {
    private Double totalDistance;
    private Long totalExplorationTime;
    private Integer streak;
    private Integer level;
    private Integer xp;
    private Integer xpForNextLevel;
    private Integer coins;
    private Integer totalDiscoveries;
    private Integer totalQuestsCompleted;
    private Double todayDistance;
    private Long todayExplorationTime;
    private List<BadgeDto> badges;

    public UserStatsDto() {
    }

    // Getters and Setters
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

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
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

    public Integer getTotalDiscoveries() {
        return totalDiscoveries;
    }

    public void setTotalDiscoveries(Integer totalDiscoveries) {
        this.totalDiscoveries = totalDiscoveries;
    }

    public Integer getTotalQuestsCompleted() {
        return totalQuestsCompleted;
    }

    public void setTotalQuestsCompleted(Integer totalQuestsCompleted) {
        this.totalQuestsCompleted = totalQuestsCompleted;
    }

    public Double getTodayDistance() {
        return todayDistance;
    }

    public void setTodayDistance(Double todayDistance) {
        this.todayDistance = todayDistance;
    }

    public Long getTodayExplorationTime() {
        return todayExplorationTime;
    }

    public void setTodayExplorationTime(Long todayExplorationTime) {
        this.todayExplorationTime = todayExplorationTime;
    }

    public List<BadgeDto> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeDto> badges) {
        this.badges = badges;
    }
}
