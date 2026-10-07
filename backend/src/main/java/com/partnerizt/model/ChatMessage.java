package com.partnerizt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "companion_id", nullable = false, length = 60)
    private String companionId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SenderType sender = SenderType.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    public ChatMessage() {
    }

    public ChatMessage(Long userId, String companionId, String message, SenderType sender) {
        this.userId = userId;
        this.companionId = companionId;
        this.message = message;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(Long userId, String companionId, String message, SenderType sender, String metadataJson) {
        this.userId = userId;
        this.companionId = companionId;
        this.message = message;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.metadataJson = metadataJson;
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
