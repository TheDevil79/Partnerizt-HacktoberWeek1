package com.partnerizt.ai;

import com.partnerizt.dto.IdentifyPhotoResponse;
import com.partnerizt.model.ChatMessage;
import com.partnerizt.model.Companion;

import java.util.List;

public interface AiService {
    
    /**
     * Generate an in-character response from a learning companion based on user queries,
     * outdoor observations, and conversation context.
     */
    String generateCompanionReply(
            Companion companion,
            String userMessage,
            List<ChatMessage> conversationHistory,
            String contextDiscoveryTitle,
            String contextPhotoUrl
    );

    /**
     * Identify an outdoor finding/photo and provide rich educational domain knowledge.
     */
    IdentifyPhotoResponse identifyOutdoorFinding(
            Companion companion,
            String photoBase64,
            String photoUrl,
            String userNotes,
            Double latitude,
            Double longitude
    );
}
