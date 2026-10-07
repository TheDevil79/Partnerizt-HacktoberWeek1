package com.partnerizt.dto;

import java.util.ArrayList;
import java.util.List;

public class IdentifyPhotoResponse {
    private boolean isIdentified = true;
    private String identifiedTitle;
    private String scientificName;
    private String category;
    private String domain;
    private String explanation;
    private List<String> funFacts = new ArrayList<>();
    private String companionCommentary;
    private String suggestedChallenge;
    private List<KnowledgeSourceDto> sources = new ArrayList<>();
    private Double confidence;
    private String rarity;
    private Integer xpValue;
    private Integer coinsValue;
    private String safetyDisclaimer;
    private Long discoveryId;

    public IdentifyPhotoResponse() {
    }

    public static IdentifyPhotoResponse lowConfidence(String message) {
        IdentifyPhotoResponse res = new IdentifyPhotoResponse();
        res.setIdentified(false);
        res.setIdentifiedTitle("Uncertain Specimen");
        res.setExplanation(message != null ? message : "I couldn't confidently identify this. Try taking a clearer photo.");
        res.setConfidence(0.35);
        res.setXpValue(0);
        res.setCoinsValue(0);
        return res;
    }

    // Getters and Setters
    public boolean isIdentified() {
        return isIdentified;
    }

    public void setIdentified(boolean identified) {
        isIdentified = identified;
    }

    public String getIdentifiedTitle() {
        return identifiedTitle;
    }

    public void setIdentifiedTitle(String identifiedTitle) {
        this.identifiedTitle = identifiedTitle;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getFunFacts() {
        return funFacts;
    }

    public void setFunFacts(List<String> funFacts) {
        this.funFacts = funFacts != null ? funFacts : new ArrayList<>();
    }

    public String getCompanionCommentary() {
        return companionCommentary;
    }

    public void setCompanionCommentary(String companionCommentary) {
        this.companionCommentary = companionCommentary;
    }

    public String getSuggestedChallenge() {
        return suggestedChallenge;
    }

    public void setSuggestedChallenge(String suggestedChallenge) {
        this.suggestedChallenge = suggestedChallenge;
    }

    public List<KnowledgeSourceDto> getSources() {
        return sources;
    }

    public void setSources(List<KnowledgeSourceDto> sources) {
        this.sources = sources != null ? sources : new ArrayList<>();
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public Integer getXpValue() {
        return xpValue;
    }

    public void setXpValue(Integer xpValue) {
        this.xpValue = xpValue;
    }

    public Integer getCoinsValue() {
        return coinsValue;
    }

    public void setCoinsValue(Integer coinsValue) {
        this.coinsValue = coinsValue;
    }

    public String getSafetyDisclaimer() {
        return safetyDisclaimer;
    }

    public void setSafetyDisclaimer(String safetyDisclaimer) {
        this.safetyDisclaimer = safetyDisclaimer;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }
}
