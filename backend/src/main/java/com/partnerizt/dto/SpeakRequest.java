package com.partnerizt.dto;

public class SpeakRequest {
    private String text;
    private String companion;

    public SpeakRequest() {}

    public SpeakRequest(String text, String companion) {
        this.text = text;
        this.companion = companion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String companion) {
        this.companion = companion;
    }
}
