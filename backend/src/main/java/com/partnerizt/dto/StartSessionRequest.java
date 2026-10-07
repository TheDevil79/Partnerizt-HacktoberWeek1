package com.partnerizt.dto;

public class StartSessionRequest {
    private Long userId;
    private Long activeQuestId;

    public StartSessionRequest() {
    }

    public StartSessionRequest(Long userId, Long activeQuestId) {
        this.userId = userId;
        this.activeQuestId = activeQuestId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActiveQuestId() {
        return activeQuestId;
    }

    public void setActiveQuestId(Long activeQuestId) {
        this.activeQuestId = activeQuestId;
    }
}
