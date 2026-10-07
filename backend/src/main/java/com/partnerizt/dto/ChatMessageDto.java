package com.partnerizt.dto;

import com.partnerizt.model.SenderType;
import java.time.LocalDateTime;

public class ChatMessageDto {
    private Long id;
    private Long userId;
    private String companionId;
    private String message;
    private SenderType sender;
    private LocalDateTime timestamp;
    private String metadataJson;

    public ChatMessageDto() {
    }

    public ChatMessageDto(Long id, Long userId, String companionId, String message, SenderType sender, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.companionId = companionId;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
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

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SenderType getSender() {
        return sender;
    }

    public void setSender(SenderType sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
