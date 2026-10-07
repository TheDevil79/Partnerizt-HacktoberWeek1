package com.partnerizt.ai.serpapi;

public interface SerpApiService {
    
    /**
     * Searches SerpApi for factual context and real-world web sources about the identified subject.
     */
    SerpApiResult searchKnowledge(String subject, String category);
}
