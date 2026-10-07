package com.partnerizt.dto;

public class UpdateSessionRequest {
    private Double distanceCovered; // cumulative meters
    private Long durationSeconds; // cumulative seconds
    private Double currentLatitude;
    private Double currentLongitude;
    private Double speed;

    public UpdateSessionRequest() {
    }

    // Getters and Setters
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

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
