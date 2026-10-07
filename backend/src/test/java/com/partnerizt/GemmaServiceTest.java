package com.partnerizt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerizt.ai.gemma.GemmaServiceImpl;
import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.model.Companion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GemmaServiceTest {

    private GemmaServiceImpl gemmaService;
    private RestTemplate mockRestTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockRestTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        gemmaService = new GemmaServiceImpl(mockRestTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should return empty / zero confidence result when image is null or empty")
    void testNullImageHandling() {
        GemmaVisionResult result = gemmaService.identifyImage(null, "nature");
        assertNotNull(result);
        assertEquals(0.0, result.getConfidence());
    }

    @Test
    @DisplayName("Should parse native Google Gemma 4 generateContent multimodal response into structured output")
    void testNativeGemmaApiResponseParsing() {
        ReflectionTestUtils.setField(gemmaService, "gemmaApiKey", "AIzaSyTestKey123");
        ReflectionTestUtils.setField(gemmaService, "gemmaApiEndpoint", "https://generativelanguage.googleapis.com/v1beta");
        ReflectionTestUtils.setField(gemmaService, "gemmaModelName", "gemma-4-31b-it");

        String mockGemmaJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "{\\"name\\":\\"Indian Palm Squirrel\\",\\"scientificName\\":\\"Funambulus palmarum\\",\\"category\\":\\"animal\\",\\"confidence\\":0.93,\\"description\\":\\"Three distinct pale dorsal stripes running down a grayish-brown coat with a bushy tail.\\"}"
                  }
                ]
              }
            }
          ]
        }
        """;

        when(mockRestTemplate.postForEntity(contains("gemma-4-31b-it"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockGemmaJson, HttpStatus.OK));

        GemmaVisionResult result = gemmaService.identifyImage("data:image/jpeg;base64,sampleBase64", "Wildlife discovery", "req_test_123");

        assertNotNull(result);
        assertEquals("Indian Palm Squirrel", result.getName());
        assertEquals("Funambulus palmarum", result.getScientificName());
        assertEquals("animal", result.getCategory());
        assertEquals(0.93, result.getConfidence(), 0.001);
        assertTrue(result.getDescription().contains("dorsal stripes"));
    }

    @Test
    @DisplayName("Should parse markdown-fenced JSON from Gemma 4 model output")
    void testMarkdownFencedJsonParsing() {
        ReflectionTestUtils.setField(gemmaService, "gemmaApiKey", "AIzaSyTestKey123");
        ReflectionTestUtils.setField(gemmaService, "gemmaApiEndpoint", "https://generativelanguage.googleapis.com/v1beta");
        ReflectionTestUtils.setField(gemmaService, "gemmaModelName", "gemma-4-31b-it");

        String mockMarkdownJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "```json\\n{\\n  \\"name\\": \\"Indian Peafowl\\",\\n  \\"scientificName\\": \\"Pavo cristatus\\",\\n  \\"category\\": \\"bird\\",\\n  \\"confidence\\": 0.98,\\n  \\"description\\": \\"Vibrant iridescent blue plumage with fan-shaped crested crown.\\"\\n}\\n```"
                  }
                ]
              }
            }
          ]
        }
        """;

        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockMarkdownJson, HttpStatus.OK));

        GemmaVisionResult result = gemmaService.identifyImage("data:image/jpeg;base64,sampleBase64", "Bird spotting", "req_test_456");

        assertNotNull(result);
        assertEquals("Indian Peafowl", result.getName());
        assertEquals("Pavo cristatus", result.getScientificName());
        assertEquals("bird", result.getCategory());
        assertEquals(0.98, result.getConfidence(), 0.001);
    }

    @Test
    @DisplayName("Should return low confidence (< 0.60) Uncertain Specimen in fallback mode when Gemma API fails")
    void testFallbackOnGemmaApiErrorReturnsLowConfidence() {
        ReflectionTestUtils.setField(gemmaService, "gemmaApiKey", "AIzaSyInvalidKey");
        ReflectionTestUtils.setField(gemmaService, "gemmaModelName", "gemma-4-31b-it");

        when(mockRestTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API Connection Refused"));

        GemmaVisionResult result = gemmaService.identifyImage("data:image/jpeg;base64,sample", "English Oak leaf with acorns", "req_test_789");
        assertNotNull(result);
        assertEquals("Uncertain Specimen", result.getName());
        assertTrue(result.getConfidence() < 0.60, "Fallback must return confidence < 0.60 and never hardcoded species");
    }

    @Test
    @DisplayName("Should return low confidence (< 0.60) when GEMMA_API_KEY is missing")
    void testMissingApiKeyReturnsLowConfidence() {
        ReflectionTestUtils.setField(gemmaService, "gemmaApiKey", "");

        GemmaVisionResult result = gemmaService.identifyImage("data:image/jpeg;base64,unclear_bytes", "Random outdoor photo", "req_test_abc");
        assertNotNull(result);
        assertEquals("Uncertain Specimen", result.getName());
        assertTrue(result.getConfidence() < 0.60, "Confidence must be < 0.60 for fallback specimens to trigger retake");
    }

    @Test
    @DisplayName("Should generate chat response with Gemma 4 model")
    void testGenerateCharacterResponseWithGemma() {
        ReflectionTestUtils.setField(gemmaService, "gemmaApiKey", "AIzaSyTestKey123");
        ReflectionTestUtils.setField(gemmaService, "gemmaModelName", "gemma-4-31b-it");

        String mockChatJson = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "Ornithological insight: Birds chirp in the morning to mark their territory and communicate when air is still."
                  }
                ]
              }
            }
          ]
        }
        """;

        when(mockRestTemplate.postForEntity(contains("gemma-4-31b-it"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockChatJson, HttpStatus.OK));

        Companion birdo = new Companion("birdo", "Birdo", "Wildlife Expert", "Birds", "Songs", "Scout", "wildlife", "green", "Energetic", "You are Birdo.");
        String reply = gemmaService.generateCharacterResponse(birdo, "Why do birds sing at sunrise?", List.of(), "Morning Warbler");

        assertNotNull(reply);
        assertTrue(reply.contains("Ornithological insight"));
    }
}
