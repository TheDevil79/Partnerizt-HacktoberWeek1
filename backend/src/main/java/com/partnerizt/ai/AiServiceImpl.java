package com.partnerizt.ai;

import com.partnerizt.ai.character.CharacterDiscoveryOutput;
import com.partnerizt.ai.character.CharacterPersonalityService;
import com.partnerizt.ai.gemma.GemmaService;
import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.KnowledgeSource;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.ai.serpapi.SerpApiService;
import com.partnerizt.dto.IdentifyPhotoResponse;
import com.partnerizt.dto.KnowledgeSourceDto;
import com.partnerizt.model.ChatMessage;
import com.partnerizt.model.Companion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * High-level AI facade orchestrating Gemma vision model, SerpApi factual enrichment,
 * and character persona synthesis for Partnerizt outdoor discovery.
 */
@Service
public class AiServiceImpl implements AiService {

    private final GemmaService gemmaService;
    private final SerpApiService serpApiService;
    private final CharacterPersonalityService characterPersonalityService;

    public AiServiceImpl(GemmaService gemmaService,
                          SerpApiService serpApiService,
                          CharacterPersonalityService characterPersonalityService) {
        this.gemmaService = gemmaService;
        this.serpApiService = serpApiService;
        this.characterPersonalityService = characterPersonalityService;
    }

    @Override
    public String generateCompanionReply(
            Companion companion,
            String userMessage,
            List<ChatMessage> conversationHistory,
            String contextDiscoveryTitle,
            String contextPhotoUrl
    ) {
        String response = gemmaService.generateCharacterResponse(companion, userMessage, conversationHistory, contextDiscoveryTitle);
        if (response != null && !response.isBlank()) {
            return response;
        }

        // Factual grounding via SerpApi with actual user query
        SerpApiResult serpApi = serpApiService.searchKnowledge(userMessage, companion != null ? companion.getDomain() : "nature");
        return characterPersonalityService.generateChatReply(companion, userMessage, conversationHistory, serpApi);
    }

    @Override
    public IdentifyPhotoResponse identifyOutdoorFinding(
            Companion companion,
            String photoBase64,
            String photoUrl,
            String userNotes,
            Double latitude,
            Double longitude
    ) {
        String photoData = photoBase64 != null && !photoBase64.isBlank() ? photoBase64 : photoUrl;
        GemmaVisionResult vision = gemmaService.identifyImage(photoData, userNotes);

        if (vision.getConfidence() == null || vision.getConfidence() < 0.60) {
            return IdentifyPhotoResponse.lowConfidence(
                    "I couldn't confidently identify this outdoor specimen. Try taking a clearer photo closer to the subject."
            );
        }

        SerpApiResult serp = serpApiService.searchKnowledge(vision.getName(), vision.getCategory());
        CharacterDiscoveryOutput charOutput = characterPersonalityService.formatDiscoveryOutput(companion, vision, serp);

        IdentifyPhotoResponse res = new IdentifyPhotoResponse();
        res.setIdentified(true);
        res.setIdentifiedTitle(vision.getName());
        res.setScientificName(vision.getScientificName());
        res.setCategory(vision.getCategory());
        res.setDomain(charOutput.getDomainTitle());
        res.setExplanation(charOutput.getDetailedExplanation());
        res.setFunFacts(charOutput.getCuratedFacts());
        res.setCompanionCommentary(charOutput.getCommentary());
        res.setSuggestedChallenge(charOutput.getSuggestedOutdoorChallenge());
        res.setSafetyDisclaimer(charOutput.getSafetyDisclaimer());
        res.setConfidence(vision.getConfidence());
        res.setRarity("UNCOMMON");
        res.setXpValue(45);
        res.setCoinsValue(10);

        List<KnowledgeSourceDto> sourceDtos = serp.getSources().stream()
                .map(s -> new KnowledgeSourceDto(s.getTitle(), s.getUrl(), s.getSnippet(), s.getSourceName()))
                .collect(Collectors.toList());
        res.setSources(sourceDtos);

        return res;
    }
}
