package com.partnerizt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerizt.ai.elevenlabs.ElevenLabsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ElevenLabsServiceTest {

    private RestTemplate mockRestTemplate;
    private ObjectMapper objectMapper;
    private ElevenLabsServiceImpl elevenLabsService;

    @BeforeEach
    void setUp() {
        mockRestTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        elevenLabsService = new ElevenLabsServiceImpl(mockRestTemplate, objectMapper);
        elevenLabsService.setDefaultVoiceId("21m00Tcm4TlvDq8ikWAM");
        elevenLabsService.setVoiceBirdo("IKne3meq5aSn9XLyUdCD");
        elevenLabsService.setVoiceFlora("EXAVITQu4vr4xnSDxMaL");
        elevenLabsService.setVoiceAtlas("VR6AewLTigWG4xSOukaG");
        elevenLabsService.setVoiceMunch("pNInz6obpgDQGcFmaJgB");
        elevenLabsService.setVoiceNova("AZnzlk1XvdvUeBnXmlld");
        elevenLabsService.setModelId("eleven_multilingual_v2");
    }

    @Test
    @DisplayName("Should resolve correct companion voice IDs with fallback")
    void testVoiceResolution() {
        assertEquals("IKne3meq5aSn9XLyUdCD", elevenLabsService.resolveVoiceId("BIRDO"));
        assertEquals("IKne3meq5aSn9XLyUdCD", elevenLabsService.resolveVoiceId("birdo"));
        assertEquals("EXAVITQu4vr4xnSDxMaL", elevenLabsService.resolveVoiceId("flora"));
        assertEquals("VR6AewLTigWG4xSOukaG", elevenLabsService.resolveVoiceId("atlas"));
        assertEquals("pNInz6obpgDQGcFmaJgB", elevenLabsService.resolveVoiceId("munch"));
        assertEquals("AZnzlk1XvdvUeBnXmlld", elevenLabsService.resolveVoiceId("nova"));
        assertEquals("21m00Tcm4TlvDq8ikWAM", elevenLabsService.resolveVoiceId("unknown_character"));
        assertEquals("21m00Tcm4TlvDq8ikWAM", elevenLabsService.resolveVoiceId(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when text is null or empty")
    void testEmptyTextHandling() {
        elevenLabsService.setApiKey("test-api-key");
        assertThrows(IllegalArgumentException.class, () -> elevenLabsService.generateSpeech(null, "BIRDO"));
        assertThrows(IllegalArgumentException.class, () -> elevenLabsService.generateSpeech("   ", "BIRDO"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when API key is missing")
    void testMissingApiKeyHandling() {
        elevenLabsService.setApiKey(null);
        assertThrows(IllegalStateException.class, () ->
                elevenLabsService.generateSpeech("Hello bird!", "BIRDO")
        );
    }

    @Test
    @DisplayName("Should return MP3 audio bytes on successful TTS response")
    void testSuccessfulTtsResponse() {
        elevenLabsService.setApiKey("valid-elevenlabs-key");
        byte[] expectedAudioBytes = new byte[]{ (byte) 0xFF, (byte) 0xFB, (byte) 0x90, 0x44 };

        when(mockRestTemplate.exchange(
                contains("https://api.elevenlabs.io/v1/text-to-speech/IKne3meq5aSn9XLyUdCD"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(new ResponseEntity<>(expectedAudioBytes, HttpStatus.OK));

        byte[] result = elevenLabsService.generateSpeech("Whoa! Look at that Indian palm squirrel!", "BIRDO");

        assertNotNull(result);
        assertArrayEquals(expectedAudioBytes, result);
    }

    @Test
    @DisplayName("Should handle ElevenLabs API failures by throwing RuntimeException")
    void testApiFailureHandling() {
        elevenLabsService.setApiKey("valid-elevenlabs-key");

        when(mockRestTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                elevenLabsService.generateSpeech("Hello world", "FLORA")
        );

        assertTrue(exception.getMessage().contains("failed") || exception.getMessage().contains("Unauthorized"));
    }
}
