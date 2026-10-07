package com.partnerizt;

import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.ai.serpapi.SerpApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SerpApiServiceTest {

    private SerpApiServiceImpl serpApiService;

    @BeforeEach
    void setUp() {
        serpApiService = new SerpApiServiceImpl();
    }

    @Test
    @DisplayName("Should return empty result when subject is null or empty")
    void testNullSubjectHandling() {
        SerpApiResult result = serpApiService.searchKnowledge(null, "nature");
        assertNotNull(result);
        assertEquals("Unknown Specimen", result.getSubject());
        assertTrue(result.getFacts().isEmpty());
        assertTrue(result.getSources().isEmpty());
    }

    @Test
    @DisplayName("Should return structured facts and verified sources for any identified subject")
    void testSquirrelKnowledgeRetrieval() {
        SerpApiResult result = serpApiService.searchKnowledge("Indian Palm Squirrel", "animal");

        assertNotNull(result);
        assertEquals("Indian Palm Squirrel", result.getSubject());
        assertFalse(result.getFacts().isEmpty());
        assertFalse(result.getSources().isEmpty());
        assertTrue(result.getSources().stream().allMatch(s -> s.getTitle() != null && !s.getTitle().isBlank()));
    }

    @Test
    @DisplayName("Should return structured facts and verified sources for English Oak")
    void testOakKnowledgeRetrieval() {
        SerpApiResult result = serpApiService.searchKnowledge("English Oak", "plant");

        assertNotNull(result);
        assertEquals("English Oak", result.getSubject());
        assertFalse(result.getFacts().isEmpty());
        assertFalse(result.getSources().isEmpty());
    }
}
