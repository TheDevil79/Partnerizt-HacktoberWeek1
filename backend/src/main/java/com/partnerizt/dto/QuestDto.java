package com.partnerizt.dto;

import com.partnerizt.model.QuestStatus;
import java.time.LocalDateTime;

public class QuestDto {
    private Long id;
    private String title;
    private String description;
    private String domain;
    private Integer xpReward;
    private Integer coinReward;
    private Double targetDistance;
    private Integer timeLimitMinutes;
    private String companionId;
    private QuestStatus status;
    private Boolean requiresPhoto;
    private String photoTargetDescription;
    private String targetLocation;
    private String difficulty;
    private Boolean isDaily;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long remainingSeconds;

    public QuestDto() {
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

    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
