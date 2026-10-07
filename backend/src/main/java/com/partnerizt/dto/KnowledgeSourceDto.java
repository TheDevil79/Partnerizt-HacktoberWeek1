package com.partnerizt.dto;

public class KnowledgeSourceDto {
    private String title;
    private String url;
    private String snippet;
    private String sourceName;

    public KnowledgeSourceDto() {
    }

    public KnowledgeSourceDto(String title, String url, String snippet, String sourceName) {
        this.title = title;
        this.url = url;
        this.snippet = snippet;
        this.sourceName = sourceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
