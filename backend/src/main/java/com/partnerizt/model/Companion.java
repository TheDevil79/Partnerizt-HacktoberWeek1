package com.partnerizt.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companions")
public class Companion {

    @Id
    @Column(length = 60)
    private String id; // e.g. "oakley", "solara", "geode", "archimedes", "nova"

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 80)
    private String domain; // e.g. "Botany & Flora", "Urban Ecology & Wildlife", "Geology & Earth Systems", "Architectural History", "Atmosphere & Astronomy"

    @Column(nullable = false, length = 150)
    private String expertise;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_type", length = 50)
    private String avatarType;

    @Column(name = "avatar_color", length = 30)
    private String avatarColor;

    @Column(length = 100)
    private String personality;

    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    private String systemPrompt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "companion_activities", joinColumns = @JoinColumn(name = "companion_id"))
    @Column(name = "activity", columnDefinition = "TEXT")
    private List<String> sampleOutdoorActivities = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "companion_starters", joinColumns = @JoinColumn(name = "companion_id"))
    @Column(name = "starter_prompt", columnDefinition = "TEXT")
    private List<String> promptStarters = new ArrayList<>();

    public Companion() {
    }

    public Companion(String id, String name, String title, String domain, String expertise, String description,
                     String avatarType, String avatarColor, String personality, String systemPrompt) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.domain = domain;
        this.expertise = expertise;
        this.description = description;
        this.avatarType = avatarType;
        this.avatarColor = avatarColor;
        this.personality = personality;
        this.systemPrompt = systemPrompt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public List<String> getSampleOutdoorActivities() {
        return sampleOutdoorActivities;
    }

    public void setSampleOutdoorActivities(List<String> sampleOutdoorActivities) {
        this.sampleOutdoorActivities = sampleOutdoorActivities;
    }

    public List<String> getPromptStarters() {
        return promptStarters;
    }

    public void setPromptStarters(List<String> promptStarters) {
        this.promptStarters = promptStarters;
    }
}
