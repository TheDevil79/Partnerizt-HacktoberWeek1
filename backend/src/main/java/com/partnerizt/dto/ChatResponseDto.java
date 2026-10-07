package com.partnerizt.dto;

public class ChatResponseDto {
    private ChatMessageDto userMessage;
    private ChatMessageDto companionMessage;
    private String companionId;
    private String companionName;

    public ChatResponseDto() {
    }

    public ChatResponseDto(ChatMessageDto userMessage, ChatMessageDto companionMessage, String companionId, String companionName) {
        this.userMessage = userMessage;
        this.companionMessage = companionMessage;
        this.companionId = companionId;
        this.companionName = companionName;
    }

    // Getters and Setters
    public ChatMessageDto getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(ChatMessageDto userMessage) {
        this.userMessage = userMessage;
    }

    public ChatMessageDto getCompanionMessage() {
        return companionMessage;
    }

    public void setCompanionMessage(ChatMessageDto companionMessage) {
        this.companionMessage = companionMessage;
    }

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public String getCompanionName() {
        return companionName;
    }

    public void setCompanionName(String companionName) {
        this.companionName = companionName;
    }
}
