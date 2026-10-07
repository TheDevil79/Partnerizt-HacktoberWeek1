package com.partnerizt.ai.character;

import java.util.List;

public class CharacterDiscoveryOutput {
    private String characterName;
    private String domainTitle;
    private String commentary;
    private String detailedExplanation;
    private String suggestedOutdoorChallenge;
    private String safetyDisclaimer;
    private List<String> curatedFacts;

    public CharacterDiscoveryOutput() {
    }

    public CharacterDiscoveryOutput(String characterName, String domainTitle, String commentary,
                                    String detailedExplanation, String suggestedOutdoorChallenge,
                                    String safetyDisclaimer, List<String> curatedFacts) {
        this.characterName = characterName;
        this.domainTitle = domainTitle;
        this.commentary = commentary;
        this.detailedExplanation = detailedExplanation;
        this.suggestedOutdoorChallenge = suggestedOutdoorChallenge;
        this.safetyDisclaimer = safetyDisclaimer;
        this.curatedFacts = curatedFacts;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getDomainTitle() {
        return domainTitle;
    }

    public void setDomainTitle(String domainTitle) {
        this.domainTitle = domainTitle;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getDetailedExplanation() {
        return detailedExplanation;
    }

    public void setDetailedExplanation(String detailedExplanation) {
        this.detailedExplanation = detailedExplanation;
    }

    public String getSuggestedOutdoorChallenge() {
        return suggestedOutdoorChallenge;
    }

    public void setSuggestedOutdoorChallenge(String suggestedOutdoorChallenge) {
        this.suggestedOutdoorChallenge = suggestedOutdoorChallenge;
    }

    public String getSafetyDisclaimer() {
        return safetyDisclaimer;
    }

    public void setSafetyDisclaimer(String safetyDisclaimer) {
        this.safetyDisclaimer = safetyDisclaimer;
    }

    public List<String> getCuratedFacts() {
        return curatedFacts;
    }

    public void setCuratedFacts(List<String> curatedFacts) {
        this.curatedFacts = curatedFacts;
    }
}
