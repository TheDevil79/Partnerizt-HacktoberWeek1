package com.partnerizt.dto;

public class EndSessionRequest {
    private Double finalDistance;
    private Long finalDurationSeconds;

    public EndSessionRequest() {
    }

    public EndSessionRequest(Double finalDistance, Long finalDurationSeconds) {
        this.finalDistance = finalDistance;
        this.finalDurationSeconds = finalDurationSeconds;
    }

    // Getters and Setters
    public Double getFinalDistance() {
        return finalDistance;
    }

    public void setFinalDistance(Double finalDistance) {
        this.finalDistance = finalDistance;
    }

    public Long getFinalDurationSeconds() {
        return finalDurationSeconds;
    }

    public void setFinalDurationSeconds(Long finalDurationSeconds) {
        this.finalDurationSeconds = finalDurationSeconds;
    }
}
