package com.partnerizt;

import com.partnerizt.ai.character.CharacterDiscoveryOutput;
import com.partnerizt.ai.character.CharacterPersonalityServiceImpl;
import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.model.Companion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterPersonalityServiceTest {

    private CharacterPersonalityServiceImpl characterService;

    @BeforeEach
    void setUp() {
        characterService = new CharacterPersonalityServiceImpl();
    }

    @Test
    @DisplayName("Birdo should produce energetic wildlife commentary and 30-second observation challenge")
    void testBirdoPersonality() {
        Companion birdo = new Companion("birdo", "Birdo", "Wildlife Specialist", "Wildlife & Animal Behavior", "Birds", "Bio", "wildlife", "amber", "Energetic", "System");
        GemmaVisionResult vision = new GemmaVisionResult("Indian Roller", "Coracias benghalensis", "bird", 0.95, "Blue wings");
        SerpApiResult serp = new SerpApiResult("Indian Roller", List.of("Famous for rolling aerial dives in courtship."), List.of());

        CharacterDiscoveryOutput out = characterService.formatDiscoveryOutput(birdo, vision, serp);

        assertNotNull(out);
        assertEquals("Birdo", out.getCharacterName());
        assertTrue(out.getCommentary().contains("🐦"));
        assertTrue(out.getCommentary().contains("Indian Roller"));
        assertTrue(out.getSuggestedOutdoorChallenge().contains("30 seconds"));
        assertNull(out.getSafetyDisclaimer());
    }

    @Test
    @DisplayName("Flora should produce botanical commentary and leaf venation challenge")
    void testFloraPersonality() {
        Companion flora = new Companion("flora", "Flora", "Botanist", "Botany & Dendrology", "Plants", "Bio", "botanist", "emerald", "Calm", "System");
        GemmaVisionResult vision = new GemmaVisionResult("English Oak", "Quercus robur", "plant", 0.96, "Lobed leaf");
        SerpApiResult serp = new SerpApiResult("English Oak", List.of("Supports over 2,300 species of wildlife."), List.of());

        CharacterDiscoveryOutput out = characterService.formatDiscoveryOutput(flora, vision, serp);

        assertNotNull(out);
        assertEquals("Flora", out.getCharacterName());
        assertTrue(out.getCommentary().contains("🌿"));
        assertTrue(out.getSuggestedOutdoorChallenge().contains("venation"));
        assertNull(out.getSafetyDisclaimer());
    }

    @Test
    @DisplayName("Atlas should produce architectural history commentary and masonry challenge")
    void testAtlasPersonality() {
        Companion atlas = new Companion("atlas", "Atlas", "Architectural Historian", "Architecture & Heritage", "Masonry", "Bio", "architect", "indigo", "Adventurous", "System");
        GemmaVisionResult vision = new GemmaVisionResult("Lancet Arch", "Gothic Revival", "architecture", 0.97, "Stone arch with keystone");
        SerpApiResult serp = new SerpApiResult("Lancet Arch", List.of("Pointed arches channel weight downward steeply."), List.of());

        CharacterDiscoveryOutput out = characterService.formatDiscoveryOutput(atlas, vision, serp);

        assertNotNull(out);
        assertEquals("Atlas", out.getCharacterName());
        assertTrue(out.getCommentary().contains("🏛️"));
        assertTrue(out.getSuggestedOutdoorChallenge().contains("masonry"));
        assertNull(out.getSafetyDisclaimer());
    }

    @Test
    @DisplayName("Munch MUST produce mandatory foraging safety disclaimer for wild food/plants")
    void testMunchPersonalityAndSafetyDisclaimer() {
        Companion munch = new Companion("munch", "Munch", "Foraging Guide", "Wild Flora & Food Culture", "Herbs", "Bio", "forager", "rose", "Enthusiastic", "System");
        GemmaVisionResult vision = new GemmaVisionResult("Wild Rosemary", "Salvia rosmarinus", "food", 0.95, "Aromatic herb");
        SerpApiResult serp = new SerpApiResult("Wild Rosemary", List.of("Produces camphor and cineole essential oils."), List.of());

        CharacterDiscoveryOutput out = characterService.formatDiscoveryOutput(munch, vision, serp);

        assertNotNull(out);
        assertEquals("Munch", out.getCharacterName());
        assertTrue(out.getCommentary().contains("🫐"));
        assertNotNull(out.getSafetyDisclaimer());
        assertTrue(out.getSafetyDisclaimer().contains("Foraging Safety Note"));
        assertTrue(out.getSafetyDisclaimer().contains("Never ingest"));
    }

    @Test
    @DisplayName("Nova should produce geology commentary and rock comparison challenge")
    void testNovaPersonality() {
        Companion nova = new Companion("nova", "Nova", "Geologist", "Geology & Earth Systems", "Rocks", "Bio", "geologist", "purple", "Curious", "System");
        GemmaVisionResult vision = new GemmaVisionResult("Banded Gneiss", "Metamorphic Tectonite", "geology", 0.94, "Metamorphic bands");
        SerpApiResult serp = new SerpApiResult("Banded Gneiss", List.of("Formed under extreme lithostatic pressure."), List.of());

        CharacterDiscoveryOutput out = characterService.formatDiscoveryOutput(nova, vision, serp);

        assertNotNull(out);
        assertEquals("Nova", out.getCharacterName());
        assertTrue(out.getCommentary().contains("✨"));
        assertTrue(out.getSuggestedOutdoorChallenge().contains("stone"));
        assertNull(out.getSafetyDisclaimer());
    }
}
