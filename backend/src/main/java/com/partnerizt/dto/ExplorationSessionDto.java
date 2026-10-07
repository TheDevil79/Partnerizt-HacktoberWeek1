package com.partnerizt.dto;

import com.partnerizt.model.SessionStatus;
import java.time.LocalDateTime;

public class ExplorationSessionDto {
    private Long id;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double distanceCovered;
    private Long durationSeconds;
    private Long activeQuestId;
    private QuestDto activeQuest;
    private SessionStatus status;
    private Integer discoveriesCount;
    private Double averageSpeed;
    private Integer xpEarned;
    private Integer coinsEarned;

    public ExplorationSessionDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(Double distanceCovered) {
        this.distanceCovered = distanceCovered;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Long getActiveQuestId() {
        return activeQuestId;
    }

    public void setActiveQuestId(Long activeQuestId) {
        this.activeQuestId = activeQuestId;
    }

    public QuestDto getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(QuestDto activeQuest) {
        this.activeQuest = activeQuest;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Integer getDiscoveriesCount() {
        return discoveriesCount;
    }

    public void setDiscoveriesCount(Integer discoveriesCount) {
        this.discoveriesCount = discoveriesCount;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Integer getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public Integer getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(Integer coinsEarned) {
        this.coinsEarned = coinsEarned;
    }
}
