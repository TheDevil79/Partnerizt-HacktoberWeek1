package com.partnerizt.ai.elevenlabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ElevenLabsServiceImpl implements ElevenLabsService {

    private static final Logger log = LoggerFactory.getLogger(ElevenLabsServiceImpl.class);

    @Value("${ai.elevenlabs.api-key:${ELEVENLABS_API_KEY:}}")
    private String apiKey;

    @Value("${ai.elevenlabs.model-id:${ELEVENLABS_MODEL_ID:eleven_multilingual_v2}}")
    private String modelId;

    @Value("${ai.elevenlabs.default-voice-id:${ELEVENLABS_DEFAULT_VOICE_ID:21m00Tcm4TlvDq8ikWAM}}")
    private String defaultVoiceId;

    @Value("${ai.elevenlabs.voice-birdo:${ELEVENLABS_VOICE_BIRDO:IKne3meq5aSn9XLyUdCD}}")
    private String voiceBirdo;

    @Value("${ai.elevenlabs.voice-flora:${ELEVENLABS_VOICE_FLORA:EXAVITQu4vr4xnSDxMaL}}")
    private String voiceFlora;

    @Value("${ai.elevenlabs.voice-atlas:${ELEVENLABS_VOICE_ATLAS:VR6AewLTigWG4xSOukaG}}")
    private String voiceAtlas;

    @Value("${ai.elevenlabs.voice-munch:${ELEVENLABS_VOICE_MUNCH:pNInz6obpgDQGcFmaJgB}}")
    private String voiceMunch;

    @Value("${ai.elevenlabs.voice-nova:${ELEVENLABS_VOICE_NOVA:AZnzlk1XvdvUeBnXmlld}}")
    private String voiceNova;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ElevenLabsServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ElevenLabsServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        boolean configured = (this.apiKey != null && !this.apiKey.isBlank());
        log.info("[ELEVENLABS_CONFIG] ElevenLabs TTS initialized | model={} | API key configured={}", modelId, configured);
        if (!configured) {
            log.warn("[ELEVENLABS_CONFIG] ElevenLabs API key not detected. Speech generation will return unavailable status until ELEVENLABS_API_KEY is configured.");
        }
    }

    public String resolveVoiceId(String companion) {
        if (companion == null || companion.isBlank()) {
            return (defaultVoiceId != null && !defaultVoiceId.isBlank()) ? defaultVoiceId : "21m00Tcm4TlvDq8ikWAM";
        }
        String comp = companion.trim().toLowerCase();
        switch (comp) {
            case "birdo":
                return (voiceBirdo != null && !voiceBirdo.isBlank()) ? voiceBirdo : defaultVoiceId;
            case "flora":
                return (voiceFlora != null && !voiceFlora.isBlank()) ? voiceFlora : defaultVoiceId;
            case "atlas":
                return (voiceAtlas != null && !voiceAtlas.isBlank()) ? voiceAtlas : defaultVoiceId;
            case "munch":
                return (voiceMunch != null && !voiceMunch.isBlank()) ? voiceMunch : defaultVoiceId;
            case "nova":
                return (voiceNova != null && !voiceNova.isBlank()) ? voiceNova : defaultVoiceId;
            default:
                return (defaultVoiceId != null && !defaultVoiceId.isBlank()) ? defaultVoiceId : "21m00Tcm4TlvDq8ikWAM";
        }
    }

    @Override
    public byte[] generateSpeech(String text, String companion) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text content cannot be null or empty");
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[ELEVENLABS_TTS] Call rejected: ELEVENLABS_API_KEY is not configured");
            throw new IllegalStateException("ElevenLabs API key is not configured");
        }

        String voiceId = resolveVoiceId(companion);
        String endpoint = String.format("https://api.elevenlabs.io/v1/text-to-speech/%s", voiceId);

        log.info("[ELEVENLABS_TTS] Requesting speech synthesis | companion={} | voiceId={} | model={} | textLength={}",
                companion, voiceId, modelId, text.length());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("xi-api-key", apiKey.trim());
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.parseMediaType("audio/mpeg")));

            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("text", text.trim());
            bodyNode.put("model_id", (modelId != null && !modelId.isBlank()) ? modelId : "eleven_multilingual_v2");

            ObjectNode voiceSettings = bodyNode.putObject("voice_settings");
            voiceSettings.put("stability", 0.5);
            voiceSettings.put("similarity_boost", 0.75);

            String requestBody = objectMapper.writeValueAsString(bodyNode);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("[ELEVENLABS_TTS] Speech generated successfully | audioBytes={}", response.getBody().length);
                return response.getBody();
            } else {
                throw new RuntimeException("ElevenLabs returned non-2xx status: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            log.error("[ELEVENLABS_ERROR] HTTP error from ElevenLabs API: status={}", e.getStatusCode());
            throw new RuntimeException("ElevenLabs API request failed with status " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("[ELEVENLABS_ERROR] Speech synthesis failed: {}", e.getMessage());
            throw new RuntimeException("Speech synthesis failed: " + e.getMessage(), e);
        }
    }

    // Setters for testing and configuration
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setDefaultVoiceId(String defaultVoiceId) {
        this.defaultVoiceId = defaultVoiceId;
    }

    public void setVoiceBirdo(String voiceBirdo) {
        this.voiceBirdo = voiceBirdo;
    }

    public void setVoiceFlora(String voiceFlora) {
        this.voiceFlora = voiceFlora;
    }

    public void setVoiceAtlas(String voiceAtlas) {
        this.voiceAtlas = voiceAtlas;
    }

    public void setVoiceMunch(String voiceMunch) {
        this.voiceMunch = voiceMunch;
    }

    public void setVoiceNova(String voiceNova) {
        this.voiceNova = voiceNova;
    }
}
