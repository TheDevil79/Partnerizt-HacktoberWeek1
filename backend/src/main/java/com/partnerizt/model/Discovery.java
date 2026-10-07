package com.partnerizt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discoveries")
public class Discovery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "quest_id")
    private Long questId;

    @Column(name = "companion_id", nullable = false, length = 60)
    private String companionId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "scientific_name", length = 150)
    private String scientificName;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "ai_explanation", columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(name = "safety_disclaimer", columnDefinition = "TEXT")
    private String safetyDisclaimer;

    @Column(name = "confidence")
    private Double confidence = 0.95;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 150)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscoveryRarity rarity = DiscoveryRarity.COMMON;

    @Column(name = "xp_awarded", nullable = false)
    private Integer xpAwarded = 25;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Discovery() {
    }

    public Discovery(Long userId, Long sessionId, Long questId, String companionId, String title,
                     String category, String notes, String photoUrl, String aiExplanation,
                     Double latitude, Double longitude, String locationName, DiscoveryRarity rarity, Integer xpAwarded) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.questId = questId;
        this.companionId = companionId;
        this.title = title;
        this.category = category;
        this.notes = notes;
        this.photoUrl = photoUrl;
        this.aiExplanation = aiExplanation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.rarity = rarity != null ? rarity : DiscoveryRarity.COMMON;
        this.xpAwarded = xpAwarded != null ? xpAwarded : 25;
        this.confidence = 0.95;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }

    public String getSafetyDisclaimer() {
        return safetyDisclaimer;
    }

    public void setSafetyDisclaimer(String safetyDisclaimer) {
        this.safetyDisclaimer = safetyDisclaimer;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public DiscoveryRarity getRarity() {
        return rarity;
    }

    public void setRarity(DiscoveryRarity rarity) {
        this.rarity = rarity;
    }

    public Integer getXpAwarded() {
        return xpAwarded;
    }

    public void setXpAwarded(Integer xpAwarded) {
        this.xpAwarded = xpAwarded;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
