package com.partnerizt.ai.gemma;

import com.partnerizt.model.ChatMessage;
import com.partnerizt.model.Companion;

import java.util.List;

public interface GemmaService {
    
    /**
     * Identify what is in the provided outdoor image using Gemma vision capabilities.
     * Returns structured identification result.
     */
    GemmaVisionResult identifyImage(String imageData, String contextHint);
    default GemmaVisionResult identifyImage(String imageData, String contextHint, String requestId) {
        return identifyImage(imageData, contextHint);
    }

    /**
     * Generate in-character companion response using Gemma LLM.
     */
    String generateCharacterResponse(Companion companion, String userMessage, List<ChatMessage> history, String contextDiscoveryTitle);
}
