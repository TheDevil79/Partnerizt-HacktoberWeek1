package com.partnerizt.dto;

import java.util.List;

public class CompanionDto {
    private String id;
    private String name;
    private String title;
    private String domain;
    private String expertise;
    private String description;
    private String avatarType;
    private String avatarColor;
    private String personality;
    private List<String> sampleOutdoorActivities;
    private List<String> promptStarters;

    public CompanionDto() {
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
