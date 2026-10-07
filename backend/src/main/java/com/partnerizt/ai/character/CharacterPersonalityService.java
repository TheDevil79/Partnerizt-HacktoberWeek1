package com.partnerizt.ai.character;

import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.model.Companion;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CharacterPersonalityService {
    
    /**
     * Synthesize character commentary, tailored explanation, outdoor challenge, and safety notes for photo discovery.
     */
    CharacterDiscoveryOutput formatDiscoveryOutput(Companion companion, GemmaVisionResult vision, SerpApiResult serpApi);

    /**
     * Generate conversational chat response grounded in user's question, companion persona, and SerpApi knowledge.
     */
    String generateChatReply(Companion companion, String userMessage, List<com.partnerizt.model.ChatMessage> history, SerpApiResult serpApi);
}
