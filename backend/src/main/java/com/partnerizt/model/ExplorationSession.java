package com.partnerizt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exploration_sessions")
public class ExplorationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "distance_covered", nullable = false)
    private Double distanceCovered = 0.0; // In meters

    @Column(name = "duration_seconds", nullable = false)
    private Long durationSeconds = 0L; // In seconds

    @Column(name = "active_quest_id")
    private Long activeQuestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "discoveries_count", nullable = false)
    private Integer discoveriesCount = 0;

    @Column(name = "average_speed")
    private Double averageSpeed = 0.0; // In km/h or m/s

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExplorationSession() {
    }

    public ExplorationSession(Long userId, Long activeQuestId) {
        this.userId = userId;
        this.activeQuestId = activeQuestId;
        this.startTime = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
        this.distanceCovered = 0.0;
        this.durationSeconds = 0L;
        this.discoveriesCount = 0;
        this.averageSpeed = 0.0;
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
