package com.partnerizt.ai.gemma;

public class GemmaVisionResult {
    private String name;
    private String scientificName;
    private String category;
    private Double confidence;
    private String description;

    public GemmaVisionResult() {
    }

    public GemmaVisionResult(String name, String scientificName, String category, Double confidence, String description) {
        this.name = name;
        this.scientificName = scientificName;
        this.category = category;
        this.confidence = confidence;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
