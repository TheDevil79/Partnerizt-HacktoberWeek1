package com.partnerizt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(length = 120)
    private String email;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer xp = 0;

    @Column(nullable = false)
    private Integer coins = 50;

    @Column(nullable = false)
    private Integer streak = 1;

    @Column(name = "total_distance", nullable = false)
    private Double totalDistance = 0.0; // In meters

    @Column(name = "total_exploration_time", nullable = false)
    private Long totalExplorationTime = 0L; // In seconds

    @Column(name = "discoveries_count", nullable = false)
    private Integer discoveriesCount = 0;

    @Column(name = "quests_completed_count", nullable = false)
    private Integer questsCompletedCount = 0;

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.level = 1;
        this.xp = 0;
        this.coins = 50;
        this.streak = 1;
        this.totalDistance = 0.0;
        this.totalExplorationTime = 0L;
        this.discoveriesCount = 0;
        this.questsCompletedCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastActiveDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
