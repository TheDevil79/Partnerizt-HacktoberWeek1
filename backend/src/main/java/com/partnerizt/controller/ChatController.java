package com.partnerizt.controller;

import com.partnerizt.dto.ApiResponse;
import com.partnerizt.dto.ChatMessageDto;
import com.partnerizt.dto.ChatResponseDto;
import com.partnerizt.dto.SendChatMessageRequest;
import com.partnerizt.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{companionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getConversationHistory(
            @PathVariable String companionId,
            @RequestParam(required = false) Long userId
    ) {
        List<ChatMessageDto> messages = chatService.getConversationHistory(userId, companionId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/{companionId}/send")
    public ResponseEntity<ApiResponse<ChatResponseDto>> sendMessage(
            @PathVariable String companionId,
            @RequestBody SendChatMessageRequest request
    ) {
        ChatResponseDto response = chatService.sendMessage(companionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{companionId}/messages")
    public ResponseEntity<ApiResponse<Void>> clearConversation(
            @PathVariable String companionId,
            @RequestParam(required = false) Long userId
    ) {
        chatService.clearConversation(userId, companionId);
        return ResponseEntity.ok(ApiResponse.success("Conversation cleared", null));
    }
}
