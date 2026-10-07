package com.partnerizt.service;

import com.partnerizt.ai.character.CharacterDiscoveryOutput;
import com.partnerizt.ai.character.CharacterPersonalityService;
import com.partnerizt.ai.gemma.GemmaService;
import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.KnowledgeSource;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.ai.serpapi.SerpApiService;
import com.partnerizt.dto.CreateDiscoveryRequest;
import com.partnerizt.dto.DiscoveryDto;
import com.partnerizt.dto.IdentifyPhotoRequest;
import com.partnerizt.dto.IdentifyPhotoResponse;
import com.partnerizt.dto.KnowledgeSourceDto;
import com.partnerizt.exception.BadRequestException;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.Companion;
import com.partnerizt.model.Discovery;
import com.partnerizt.model.DiscoveryRarity;
import com.partnerizt.model.User;
import com.partnerizt.repository.CompanionRepository;
import com.partnerizt.repository.DiscoveryRepository;
import com.partnerizt.repository.ExplorationSessionRepository;
import com.partnerizt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private final DiscoveryRepository discoveryRepository;
    private final CompanionRepository companionRepository;
    private final ExplorationSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CompanionService companionService;
    private final GemmaService gemmaService;
    private final SerpApiService serpApiService;
    private final CharacterPersonalityService characterPersonalityService;

    public DiscoveryServiceImpl(DiscoveryRepository discoveryRepository,
                                CompanionRepository companionRepository,
                                ExplorationSessionRepository sessionRepository,
                                UserRepository userRepository,
                                UserService userService,
                                CompanionService companionService,
                                GemmaService gemmaService,
                                SerpApiService serpApiService,
                                CharacterPersonalityService characterPersonalityService) {
        this.discoveryRepository = discoveryRepository;
        this.companionRepository = companionRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.companionService = companionService;
        this.gemmaService = gemmaService;
        this.serpApiService = serpApiService;
        this.characterPersonalityService = characterPersonalityService;
    }

    @Override
    public List<DiscoveryDto> getUserDiscoveries(Long userId) {
        Long resolvedUserId = (userId != null) ? userId : userService.getOrCreateDefaultUser().getId();
        return discoveryRepository.findByUserIdOrderByCreatedAtDesc(resolvedUserId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DiscoveryDto getDiscoveryById(Long id) {
        Discovery d = discoveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discovery", "id", id));
        return mapToDto(d);
    }

    @Override
    @Transactional
    public DiscoveryDto createDiscovery(CreateDiscoveryRequest request) {
        Long userId = (request.getUserId() != null) ? request.getUserId() : userService.getOrCreateDefaultUser().getId();

        DiscoveryRarity rarity = (request.getRarity() != null) ? request.getRarity() : DiscoveryRarity.COMMON;
        int xp = switch (rarity) {
            case UNCOMMON -> 40;
            case RARE -> 75;
            case LEGENDARY -> 150;
            default -> 25;
        };

        Discovery discovery = new Discovery(
                userId,
                request.getSessionId(),
                request.getQuestId(),
                request.getCompanionId() != null ? request.getCompanionId() : "flora",
                request.getTitle() != null ? request.getTitle() : "Field Specimen",
                request.getCategory() != null ? request.getCategory() : "Outdoor Discovery",
                request.getNotes(),
                request.getPhotoUrl(),
                request.getAiExplanation(),
                request.getLatitude(),
                request.getLongitude(),
                request.getLocationName(),
                rarity,
                xp
        );

        Discovery saved = discoveryRepository.save(discovery);

        // Update user stats
        userService.addXpAndCoins(userId, xp, Math.max(1, xp / 15));
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setDiscoveriesCount(user.getDiscoveriesCount() + 1);
            userRepository.save(user);
        }

        // If part of active session, update session count
        if (request.getSessionId() != null) {
            sessionRepository.findById(request.getSessionId()).ifPresent(session -> {
                session.setDiscoveriesCount(session.getDiscoveriesCount() + 1);
                sessionRepository.save(session);
            });
        }

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public IdentifyPhotoResponse identifyPhoto(IdentifyPhotoRequest request) {
        if (request == null) {
            throw new BadRequestException("Identification request must not be null");
        }

        String photoData = request.getPhotoBase64();
        String imageFormat = "base64";
        if (photoData == null || photoData.isBlank()) {
            photoData = request.getPhotoUrl();
            imageFormat = "url";
        }

        // 1. Validate uploaded image
        if (photoData == null || photoData.trim().isEmpty()) {
            throw new BadRequestException("Image data (photoUrl or photoBase64) is required for identification");
        }

        log.info("[IMAGE_RECEIVED] Format: {} | Query: '{}' | CompanionId: {}",
                imageFormat, request.getUserQuery(), request.getCompanionId());

        String requestId = (request.getRequestId() != null && !request.getRequestId().isBlank())
                ? request.getRequestId()
                : "req_" + java.util.UUID.randomUUID().toString().substring(0, 8);

        log.info("[DISCOVERY_SERVICE] requestId={} starting identification", requestId);
        log.info("[IDENTIFY_BACKEND] requestId={} companion={} query={}",
                requestId, request.getCompanionId(), request.getUserQuery());

        // 2. Resolve companion
        String companionId = request.getCompanionId() != null ? request.getCompanionId().toLowerCase() : "flora";
        Companion companion = companionRepository.findById(companionId)
                .orElseGet(() -> companionRepository.findById("flora").orElse(null));

        // 3. Send image to Gemma vision model
        GemmaVisionResult visionResult = gemmaService.identifyImage(photoData, request.getUserQuery(), requestId);

        // 4. Check confidence threshold (< 0.60 is considered uncertain)
        if (visionResult.getConfidence() == null || visionResult.getConfidence() < 0.60) {
            log.info("[GEMMA_REJECTED] requestId={} low confidence ({} < 0.60). Returning uncertainty notice.",
                    requestId, visionResult.getConfidence());
            IdentifyPhotoResponse lowRes = IdentifyPhotoResponse.lowConfidence(
                    "I couldn't confidently identify this outdoor specimen. Try taking a closer photo with better natural lighting."
            );
            log.info("[IDENTIFY_RESPONSE] requestId={} name={} confidence={}",
                    requestId, lowRes.getIdentifiedTitle(), lowRes.getConfidence());
            return lowRes;
        }

        // 5. Query SerpApi for real-world facts and source attribution
        SerpApiResult serpResult = serpApiService.searchKnowledge(visionResult.getName(), visionResult.getCategory());
        log.info("[SERP_RESULT] requestId={} subject={} sourcesCount={}",
                requestId, serpResult.getSubject(), serpResult.getSources().size());

        // 6. Generate character-specific response
        CharacterDiscoveryOutput charOutput = characterPersonalityService.formatDiscoveryOutput(companion, visionResult, serpResult);
        log.info("[CHARACTER_GENERATED] requestId={} companion={} commentaryLength={}",
                requestId, charOutput.getCharacterName(),
                charOutput.getCommentary() != null ? charOutput.getCommentary().length() : 0);

        // Calculate rarity & gamification rewards
        DiscoveryRarity rarity = determineRarity(visionResult.getConfidence(), visionResult.getCategory());
        int xpReward = (rarity == DiscoveryRarity.RARE ? 60 : rarity == DiscoveryRarity.UNCOMMON ? 45 : 30);
        int coinReward = (rarity == DiscoveryRarity.RARE ? 15 : rarity == DiscoveryRarity.UNCOMMON ? 10 : 5);

        Long resolvedUserId = (request.getUserId() != null) ? request.getUserId() : userService.getOrCreateDefaultUser().getId();

        // 7. Persist the Discovery to the user's Field Journal
        Discovery discovery = new Discovery(
                resolvedUserId,
                request.getSessionId(),
                request.getQuestId(),
                companionId,
                visionResult.getName(),
                visionResult.getCategory(),
                request.getUserQuery(),
                request.getPhotoUrl() != null ? request.getPhotoUrl() : (photoData.startsWith("http") ? photoData : "field_specimen.jpg"),
                charOutput.getDetailedExplanation(),
                request.getLatitude(),
                request.getLongitude(),
                request.getLocationName() != null ? request.getLocationName() : "Field Discovery",
                rarity,
                xpReward
        );
        discovery.setScientificName(visionResult.getScientificName());
        discovery.setSafetyDisclaimer(charOutput.getSafetyDisclaimer());
        discovery.setConfidence(visionResult.getConfidence());

        Discovery savedDiscovery = discoveryRepository.save(discovery);
        log.info("[DISCOVERY_SAVED] requestId={} id={} title={} category={} xp=+{}",
                requestId, savedDiscovery.getId(), savedDiscovery.getTitle(), savedDiscovery.getCategory(), xpReward);

        // Update user stats & awards
        userService.addXpAndCoins(resolvedUserId, xpReward, coinReward);
        User user = userRepository.findById(resolvedUserId).orElse(null);
        if (user != null) {
            user.setDiscoveriesCount(user.getDiscoveriesCount() + 1);
            userRepository.save(user);
        }

        if (request.getSessionId() != null) {
            sessionRepository.findById(request.getSessionId()).ifPresent(session -> {
                session.setDiscoveriesCount(session.getDiscoveriesCount() + 1);
                sessionRepository.save(session);
            });
        }

        // 8. Assemble structured response with sources
        IdentifyPhotoResponse response = new IdentifyPhotoResponse();
        response.setIdentified(true);
        response.setIdentifiedTitle(visionResult.getName());
        response.setScientificName(visionResult.getScientificName());
        response.setCategory(visionResult.getCategory());
        response.setDomain(charOutput.getDomainTitle());
        response.setExplanation(charOutput.getDetailedExplanation());
        response.setFunFacts(charOutput.getCuratedFacts());
        response.setCompanionCommentary(charOutput.getCommentary());
        response.setSuggestedChallenge(charOutput.getSuggestedOutdoorChallenge());
        response.setSafetyDisclaimer(charOutput.getSafetyDisclaimer());
        response.setConfidence(visionResult.getConfidence());
        response.setRarity(rarity.name());
        response.setXpValue(xpReward);
        response.setCoinsValue(coinReward);
        response.setDiscoveryId(savedDiscovery.getId());

        List<KnowledgeSourceDto> sourceDtos = serpResult.getSources().stream()
                .map(s -> new KnowledgeSourceDto(s.getTitle(), s.getUrl(), s.getSnippet(), s.getSourceName()))
                .collect(Collectors.toList());
        response.setSources(sourceDtos);

        log.info("[IDENTIFY_RESPONSE] requestId={} name={} confidence={}",
                requestId, response.getIdentifiedTitle(), response.getConfidence());

        return response;
    }

    private DiscoveryRarity determineRarity(Double confidence, String category) {
        if (confidence != null && confidence > 0.95 && ("geology".equalsIgnoreCase(category) || "architecture".equalsIgnoreCase(category))) {
            return DiscoveryRarity.RARE;
        }
        if (confidence != null && confidence >= 0.85) {
            return DiscoveryRarity.UNCOMMON;
        }
        return DiscoveryRarity.COMMON;
    }

    private DiscoveryDto mapToDto(Discovery d) {
        DiscoveryDto dto = new DiscoveryDto();
        dto.setId(d.getId());
        dto.setUserId(d.getUserId());
        dto.setSessionId(d.getSessionId());
        dto.setQuestId(d.getQuestId());
        dto.setCompanionId(d.getCompanionId());
        dto.setTitle(d.getTitle());
        dto.setScientificName(d.getScientificName());
        dto.setCategory(d.getCategory());
        dto.setNotes(d.getNotes());
        dto.setPhotoUrl(d.getPhotoUrl());
        dto.setAiExplanation(d.getAiExplanation());
        dto.setSafetyDisclaimer(d.getSafetyDisclaimer());
        dto.setConfidence(d.getConfidence());
        dto.setLatitude(d.getLatitude());
        dto.setLongitude(d.getLongitude());
        dto.setLocationName(d.getLocationName());
        dto.setRarity(d.getRarity());
        dto.setXpAwarded(d.getXpAwarded());
        dto.setCreatedAt(d.getCreatedAt());

        if (d.getCompanionId() != null) {
            companionRepository.findById(d.getCompanionId()).ifPresent(comp -> {
                dto.setCompanion(companionService.getCompanionById(comp.getId()));
            });
        }

        return dto;
    }
}
