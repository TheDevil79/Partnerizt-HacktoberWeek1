package com.partnerizt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quests")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 80)
    private String domain;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward = 100;

    @Column(name = "coin_reward", nullable = false)
    private Integer coinReward = 20;

    @Column(name = "target_distance", nullable = false)
    private Double targetDistance = 500.0; // In meters

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes = 60; // In minutes

    @Column(name = "companion_id", nullable = false, length = 60)
    private String companionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestStatus status = QuestStatus.AVAILABLE;

    @Column(name = "requires_photo", nullable = false)
    private Boolean requiresPhoto = true;

    @Column(name = "photo_target_description", columnDefinition = "TEXT")
    private String photoTargetDescription;

    @Column(name = "target_location", length = 150)
    private String targetLocation;

    @Column(name = "difficulty", length = 20)
    private String difficulty = "MEDIUM"; // EASY, MEDIUM, HARD, LEGENDARY

    @Column(name = "is_daily", nullable = false)
    private Boolean isDaily = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public Quest() {
    }

    public Quest(String title, String description, String domain, Integer xpReward, Integer coinReward,
                 Double targetDistance, Integer timeLimitMinutes, String companionId, Boolean requiresPhoto,
                 String photoTargetDescription, String targetLocation, Boolean isDaily, LocalDateTime expiresAt) {
        this.title = title;
        this.description = description;
        this.domain = domain;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.targetDistance = targetDistance;
        this.timeLimitMinutes = timeLimitMinutes;
        this.companionId = companionId;
        this.requiresPhoto = requiresPhoto;
        this.photoTargetDescription = photoTargetDescription;
        this.targetLocation = targetLocation;
        this.isDaily = isDaily;
        this.expiresAt = expiresAt;
        this.status = QuestStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public Integer getCoinReward() {
        return coinReward;
    }

    public void setCoinReward(Integer coinReward) {
        this.coinReward = coinReward;
    }

    public Double getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(Double targetDistance) {
        this.targetDistance = targetDistance;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public Boolean getRequiresPhoto() {
        return requiresPhoto;
    }

    public void setRequiresPhoto(Boolean requiresPhoto) {
        this.requiresPhoto = requiresPhoto;
    }

    public String getPhotoTargetDescription() {
        return photoTargetDescription;
    }

    public void setPhotoTargetDescription(String photoTargetDescription) {
        this.photoTargetDescription = photoTargetDescription;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getIsDaily() {
        return isDaily;
    }

    public void setIsDaily(Boolean isDaily) {
        this.isDaily = isDaily;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
