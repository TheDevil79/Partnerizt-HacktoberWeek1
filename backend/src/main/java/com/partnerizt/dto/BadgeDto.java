package com.partnerizt.dto;

import java.time.LocalDateTime;

public class BadgeDto {
    private String id;
    private String title;
    private String description;
    private String iconName;
    private String category;
    private Double requiredValue;
    private Integer xpBonus;
    private boolean unlocked;
    private LocalDateTime unlockedAt;

    public BadgeDto() {
    }

    public BadgeDto(String id, String title, String description, String iconName, String category, Double requiredValue, Integer xpBonus, boolean unlocked, LocalDateTime unlockedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconName = iconName;
        this.category = category;
        this.requiredValue = requiredValue;
        this.xpBonus = xpBonus;
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(Double requiredValue) {
        this.requiredValue = requiredValue;
    }

    public Integer getXpBonus() {
        return xpBonus;
    }

    public void setXpBonus(Integer xpBonus) {
        this.xpBonus = xpBonus;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
