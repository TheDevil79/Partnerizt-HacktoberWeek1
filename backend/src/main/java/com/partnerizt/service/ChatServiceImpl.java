package com.partnerizt.service;

import com.partnerizt.ai.AiService;
import com.partnerizt.dto.ChatMessageDto;
import com.partnerizt.dto.ChatResponseDto;
import com.partnerizt.dto.SendChatMessageRequest;
import com.partnerizt.model.ChatMessage;
import com.partnerizt.model.Companion;
import com.partnerizt.model.SenderType;
import com.partnerizt.repository.ChatMessageRepository;
import com.partnerizt.repository.CompanionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final CompanionRepository companionRepository;
    private final UserService userService;
    private final AiService aiService;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository,
                           CompanionRepository companionRepository,
                           UserService userService,
                           AiService aiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.companionRepository = companionRepository;
        this.userService = userService;
        this.aiService = aiService;
    }

    @Override
    public List<ChatMessageDto> getConversationHistory(Long userId, String companionId) {
        Long resolvedUserId = (userId != null) ? userId : userService.getOrCreateDefaultUser().getId();
        return chatMessageRepository.findByUserIdAndCompanionIdOrderByTimestampAsc(resolvedUserId, companionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatResponseDto sendMessage(String companionId, SendChatMessageRequest request) {
        Long resolvedUserId = (request.getUserId() != null) ? request.getUserId() : userService.getOrCreateDefaultUser().getId();
        Companion companion = companionRepository.findById(companionId)
                .orElseGet(() -> companionRepository.findById("flora").orElse(null));

        // Save User Message
        ChatMessage userMsg = new ChatMessage(resolvedUserId, companionId, request.getMessage(), SenderType.USER);
        ChatMessage savedUserMsg = chatMessageRepository.save(userMsg);

        // Fetch history
        List<ChatMessage> history = chatMessageRepository.findByUserIdAndCompanionIdOrderByTimestampAsc(resolvedUserId, companionId);

        // Generate AI Companion response
        String replyText = aiService.generateCompanionReply(
                companion,
                request.getMessage(),
                history,
                request.getContextDiscoveryTitle(),
                request.getContextPhotoUrl()
        );

        // Save Companion Message
        ChatMessage companionMsg = new ChatMessage(resolvedUserId, companionId, replyText, SenderType.COMPANION);
        ChatMessage savedCompanionMsg = chatMessageRepository.save(companionMsg);

        String companionName = (companion != null) ? companion.getName() : "Companion";

        return new ChatResponseDto(
                mapToDto(savedUserMsg),
                mapToDto(savedCompanionMsg),
                companionId,
                companionName
        );
    }

    @Override
    @Transactional
    public void clearConversation(Long userId, String companionId) {
        Long resolvedUserId = (userId != null) ? userId : userService.getOrCreateDefaultUser().getId();
        chatMessageRepository.deleteByUserIdAndCompanionId(resolvedUserId, companionId);
    }

    private ChatMessageDto mapToDto(ChatMessage m) {
        return new ChatMessageDto(
                m.getId(),
                m.getUserId(),
                m.getCompanionId(),
                m.getMessage(),
                m.getSender(),
                m.getTimestamp()
        );
    }
}
