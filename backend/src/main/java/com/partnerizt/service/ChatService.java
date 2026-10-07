package com.partnerizt.service;

import com.partnerizt.dto.ChatMessageDto;
import com.partnerizt.dto.ChatResponseDto;
import com.partnerizt.dto.SendChatMessageRequest;

import java.util.List;

public interface ChatService {
    List<ChatMessageDto> getConversationHistory(Long userId, String companionId);
    ChatResponseDto sendMessage(String companionId, SendChatMessageRequest request);
    void clearConversation(Long userId, String companionId);
}
