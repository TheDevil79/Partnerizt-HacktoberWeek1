package com.partnerizt.ai;

import com.partnerizt.ai.character.CharacterPersonalityServiceImpl;
import com.partnerizt.ai.serpapi.KnowledgeSource;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.model.Companion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterChatContextTest {

    private CharacterPersonalityServiceImpl characterService;
    private Companion birdo;
    private Companion flora;
    private Companion atlas;
    private Companion munch;
    private Companion nova;

    @BeforeEach
    void setUp() {
        characterService = new CharacterPersonalityServiceImpl();
        birdo = new Companion("birdo", "Birdo", "Wildlife Specialist", "Wildlife & Animal Behavior", "Birds", "Bio", "wildlife", "amber", "Energetic", "System");
        flora = new Companion("flora", "Flora", "Botanist", "Botany & Dendrology", "Plants", "Bio", "botanist", "emerald", "Calm", "System");
        atlas = new Companion("atlas", "Atlas", "Architectural Historian", "Architecture & Heritage", "Masonry", "Bio", "architect", "indigo", "Adventurous", "System");
        munch = new Companion("munch", "Munch", "Foraging Guide", "Wild Flora & Food Culture", "Herbs", "Bio", "forager", "rose", "Enthusiastic", "System");
        nova = new Companion("nova", "Nova", "Geologist", "Geology & Earth Systems", "Rocks", "Bio", "geologist", "purple", "Curious", "System");
    }

    @Test
    @DisplayName("User asks why snakes and lizards have similar faces — must NOT produce unrelated crow canned text")
    void testSnakesAndLizardsQuestionGrounding() {
        String userQuestion = "why do snakes and lizards have similar faces";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Snakes and lizards are squamate reptiles sharing cranial morphology and Jacobson's organs."), List.of(new KnowledgeSource("Squamates", "url", "snippet", "source")));

        String reply = characterService.generateChatReply(birdo, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        // Verify unrelated canned text is NOT present
        assertFalse(reply.contains("Crows and urban wildlife are wonderfully observant"), "Must not contain unrelated canned crow text!");
        // Verify actual question topic grounding
        assertTrue(reply.toLowerCase().contains("snake") || reply.toLowerCase().contains("lizard") || reply.toLowerCase().contains("squamate") || reply.toLowerCase().contains("reptile"), "Must be grounded in snakes/lizards/reptiles");
    }

    @Test
    @DisplayName("User asks 'Why do birds migrate?' — must discuss migration and flyways")
    void testBirdMigrationGrounding() {
        String userQuestion = "Why do birds migrate?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Bird migration is an adaptive seasonal response to food shortages."), List.of());

        String reply = characterService.generateChatReply(birdo, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("migrat") || reply.toLowerCase().contains("flyway") || reply.toLowerCase().contains("season"), "Must discuss migration");
    }

    @Test
    @DisplayName("User asks 'Why do leaves change color?' — must discuss chlorophyll and pigments")
    void testLeavesChangeColorGrounding() {
        String userQuestion = "Why do leaves change color?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Chlorophyll degradation reveals carotenoids and anthocyanins."), List.of());

        String reply = characterService.generateChatReply(flora, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("chlorophyll") || reply.toLowerCase().contains("leaf") || reply.toLowerCase().contains("pigment") || reply.toLowerCase().contains("autumn") || reply.toLowerCase().contains("carotenoid"), "Must discuss leaf coloration");
    }

    @Test
    @DisplayName("User asks 'Why are old buildings made with arches?' — must discuss compression and keystones")
    void testOldBuildingsArchesGrounding() {
        String userQuestion = "Why are old buildings made with arches?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Masonry arches translate vertical gravitational weight into lateral thrust."), List.of());

        String reply = characterService.generateChatReply(atlas, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("arch") || reply.toLowerCase().contains("keystone") || reply.toLowerCase().contains("thrust") || reply.toLowerCase().contains("masonry") || reply.toLowerCase().contains("gravity"), "Must discuss arches and masonry physics");
    }

    @Test
    @DisplayName("User asks 'Is this mushroom safe to eat?' — must provide foraging safety warning")
    void testMushroomSafetyGrounding() {
        String userQuestion = "Is this mushroom safe to eat?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Never consume wild mushrooms without verified mycological identification."), List.of());

        String reply = characterService.generateChatReply(munch, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("safety") || reply.toLowerCase().contains("mushroom") || reply.toLowerCase().contains("toxic") || reply.toLowerCase().contains("mycologist"), "Must provide foraging safety advisory");
    }

    @Test
    @DisplayName("User asks 'Why does Taj Mahal looks a bit yellowish nowadays?' — must discuss pollution and marble discoloration")
    void testTajMahalYellowingGrounding() {
        String userQuestion = "Why does Taj Mahal looks a bit yellowish nowadays?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("Air pollution and acid rain discolor the white marble of the Taj Mahal."), List.of());

        String reply = characterService.generateChatReply(atlas, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("yellow") || reply.toLowerCase().contains("pollution") || reply.toLowerCase().contains("marble") || reply.toLowerCase().contains("sulfur"), "Must discuss marble yellowing and pollution");
    }

    @Test
    @DisplayName("User asks 'how old are the pyramids of Egypt?' — must discuss 4500 years and Old Kingdom")
    void testPyramidsAgeGrounding() {
        String userQuestion = "how old are the pyramids of Egypt?";
        SerpApiResult serpApi = new SerpApiResult(userQuestion, List.of("The Pyramids of Giza were built around 2500 BCE."), List.of());

        String reply = characterService.generateChatReply(atlas, userQuestion, List.of(), serpApi);

        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("4,500") || reply.toLowerCase().contains("4500") || reply.toLowerCase().contains("pyramid") || reply.toLowerCase().contains("old kingdom"), "Must discuss pyramid age");
    }
}
