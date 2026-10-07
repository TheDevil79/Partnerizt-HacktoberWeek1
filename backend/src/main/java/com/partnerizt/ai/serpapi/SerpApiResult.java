package com.partnerizt.ai.serpapi;

import java.util.ArrayList;
import java.util.List;

public class SerpApiResult {
    private String subject;
    private List<String> facts = new ArrayList<>();
    private List<KnowledgeSource> sources = new ArrayList<>();

    public SerpApiResult() {
    }

    public SerpApiResult(String subject, List<String> facts, List<KnowledgeSource> sources) {
        this.subject = subject;
        this.facts = facts != null ? facts : new ArrayList<>();
        this.sources = sources != null ? sources : new ArrayList<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getFacts() {
        return facts;
    }

    public void setFacts(List<String> facts) {
        this.facts = facts;
    }

    public List<KnowledgeSource> getSources() {
        return sources;
    }

    public void setSources(List<KnowledgeSource> sources) {
        this.sources = sources;
    }
}
