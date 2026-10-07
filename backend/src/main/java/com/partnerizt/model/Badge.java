package com.partnerizt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @Column(length = 60)
    private String id; // e.g. "first_step", "five_k_club", "nature_scout", "rock_hound", "city_historian", "week_warrior"

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(length = 30)
    private String category; // DISTANCE, DISCOVERY, STREAK, QUESTS

    @Column(name = "required_value", nullable = false)
    private Double requiredValue = 1.0;

    @Column(name = "xp_bonus", nullable = false)
    private Integer xpBonus = 50;

    public Badge() {
    }

    public Badge(String id, String title, String description, String iconName, String category, Double requiredValue, Integer xpBonus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconName = iconName;
        this.category = category;
        this.requiredValue = requiredValue;
        this.xpBonus = xpBonus;
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
}
