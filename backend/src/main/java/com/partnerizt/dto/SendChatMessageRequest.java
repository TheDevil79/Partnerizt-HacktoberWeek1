package com.partnerizt.dto;

public class SendChatMessageRequest {
    private Long userId;
    private String message;
    private String contextDiscoveryTitle;
    private String contextPhotoUrl;

    public SendChatMessageRequest() {
    }

    public SendChatMessageRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContextDiscoveryTitle() {
        return contextDiscoveryTitle;
    }

    public void setContextDiscoveryTitle(String contextDiscoveryTitle) {
        this.contextDiscoveryTitle = contextDiscoveryTitle;
    }

    public String getContextPhotoUrl() {
        return contextPhotoUrl;
    }

    public void setContextPhotoUrl(String contextPhotoUrl) {
        this.contextPhotoUrl = contextPhotoUrl;
    }
}
