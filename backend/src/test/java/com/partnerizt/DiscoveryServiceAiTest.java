package com.partnerizt;

import com.partnerizt.ai.character.CharacterDiscoveryOutput;
import com.partnerizt.ai.character.CharacterPersonalityService;
import com.partnerizt.ai.gemma.GemmaService;
import com.partnerizt.ai.gemma.GemmaVisionResult;
import com.partnerizt.ai.serpapi.KnowledgeSource;
import com.partnerizt.ai.serpapi.SerpApiResult;
import com.partnerizt.ai.serpapi.SerpApiService;
import com.partnerizt.dto.IdentifyPhotoRequest;
import com.partnerizt.dto.IdentifyPhotoResponse;
import com.partnerizt.exception.BadRequestException;
import com.partnerizt.model.Companion;
import com.partnerizt.model.Discovery;
import com.partnerizt.model.User;
import com.partnerizt.repository.CompanionRepository;
import com.partnerizt.repository.DiscoveryRepository;
import com.partnerizt.repository.ExplorationSessionRepository;
import com.partnerizt.repository.UserRepository;
import com.partnerizt.service.CompanionService;
import com.partnerizt.service.DiscoveryServiceImpl;
import com.partnerizt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscoveryServiceAiTest {

    @Mock
    private DiscoveryRepository discoveryRepository;

    @Mock
    private CompanionRepository companionRepository;

    @Mock
    private ExplorationSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private CompanionService companionService;

    @Mock
    private GemmaService gemmaService;

    @Mock
    private SerpApiService serpApiService;

    @Mock
    private CharacterPersonalityService characterPersonalityService;

    @InjectMocks
    private DiscoveryServiceImpl discoveryService;

    private Companion birdoCompanion;
    private Companion munchCompanion;
    private User testUser;

    @BeforeEach
    void setUp() {
        birdoCompanion = new Companion(
                "birdo", "Birdo", "Urban Wildlife Specialist",
                "Wildlife & Animal Behavior", "Bird vocalizations", "Scout",
                "wildlife", "amber", "Energetic", "Birdo prompt"
        );

        munchCompanion = new Companion(
                "munch", "Munch", "Wild Foraging Guide",
                "Wild Flora & Food Culture", "Aromatic herbs", "Foodie",
                "forager", "rose", "Enthusiastic", "Munch prompt"
        );

        testUser = new User("nature_scout", "explorer@partnerizt.app");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Should throw BadRequestException when image data is missing or empty")
    void testInvalidImageThrowsException() {
        IdentifyPhotoRequest request = new IdentifyPhotoRequest();
        request.setPhotoUrl("");
        request.setPhotoBase64("");

        assertThrows(BadRequestException.class, () -> {
            discoveryService.identifyPhoto(request);
        });

        verify(gemmaService, never()).identifyImage(any(), any());
    }

    @Test
    @DisplayName("Should return low-confidence response when model confidence is below threshold (< 0.60)")
    void testLowConfidenceIdentification() {
        IdentifyPhotoRequest request = new IdentifyPhotoRequest();
        request.setPhotoUrl("http://example.com/blurry.jpg");
        request.setCompanionId("birdo");

        when(companionRepository.findById("birdo")).thenReturn(Optional.of(birdoCompanion));
        when(gemmaService.identifyImage(any(), any(), any())).thenReturn(
                new GemmaVisionResult("Unidentifiable blur", "N/A", "unknown", 0.40, "Blurry image")
        );

        IdentifyPhotoResponse response = discoveryService.identifyPhoto(request);

        assertNotNull(response);
        assertFalse(response.isIdentified());
        assertEquals("Uncertain Specimen", response.getIdentifiedTitle());
        assertEquals(0, response.getXpValue());
        assertEquals(0, response.getCoinsValue());

        // Verify discovery was NOT persisted and no XP awarded
        verify(discoveryRepository, never()).save(any(Discovery.class));
        verify(userService, never()).addXpAndCoins(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should successfully execute full AI discovery pipeline and persist to Field Journal")
    void testSuccessfulCompleteDiscovery() {
        IdentifyPhotoRequest request = new IdentifyPhotoRequest();
        request.setUserId(1L);
        request.setPhotoUrl("http://example.com/roller.jpg");
        request.setCompanionId("birdo");
        request.setUserQuery("blue bird perched on branch");

        when(companionRepository.findById("birdo")).thenReturn(Optional.of(birdoCompanion));
        when(userService.getOrCreateDefaultUser()).thenReturn(testUser);

        GemmaVisionResult visionResult = new GemmaVisionResult(
                "Indian Roller", "Coracias benghalensis", "bird", 0.95, "Electric blue flight feathers"
        );
        when(gemmaService.identifyImage(eq("http://example.com/roller.jpg"), eq("blue bird perched on branch"), anyString()))
                .thenReturn(visionResult);

        KnowledgeSource source = new KnowledgeSource("IUCN Red List — Coracias benghalensis", "https://iucnredlist.org", "Distribution", "iucnredlist.org");
        SerpApiResult serpResult = new SerpApiResult(
                "Indian Roller",
                List.of("Famous for rolling aerial dives in courtship."),
                List.of(source)
        );
        when(serpApiService.searchKnowledge("Indian Roller", "bird")).thenReturn(serpResult);

        CharacterDiscoveryOutput charOutput = new CharacterDiscoveryOutput(
                "Birdo", "Wildlife & Fauna", "You found an Indian Roller! 🐦",
                "Great eyes out there! Coracias benghalensis is active.",
                "Watch it quietly for 30 seconds.", null,
                List.of("Famous for rolling aerial dives in courtship.")
        );
        when(characterPersonalityService.formatDiscoveryOutput(birdoCompanion, visionResult, serpResult))
                .thenReturn(charOutput);

        when(discoveryRepository.save(any(Discovery.class))).thenAnswer(i -> {
            Discovery d = i.getArgument(0);
            d.setId(500L);
            return d;
        });

        IdentifyPhotoResponse response = discoveryService.identifyPhoto(request);

        assertNotNull(response);
        assertTrue(response.isIdentified());
        assertEquals("Indian Roller", response.getIdentifiedTitle());
        assertEquals("Coracias benghalensis", response.getScientificName());
        assertEquals("bird", response.getCategory());
        assertEquals(500L, response.getDiscoveryId());
        assertTrue(response.getXpValue() > 0);
        assertTrue(response.getCoinsValue() > 0);

        // Verify source attribution
        assertNotNull(response.getSources());
        assertEquals(1, response.getSources().size());
        assertEquals("https://iucnredlist.org", response.getSources().get(0).getUrl());

        // Verify persistence & reward
        verify(discoveryRepository, times(1)).save(any(Discovery.class));
        verify(userService, times(1)).addXpAndCoins(eq(1L), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should include safety disclaimer when identifying food / wild foraging items (Munch)")
    void testForagingSafetyDisclaimer() {
        IdentifyPhotoRequest request = new IdentifyPhotoRequest();
        request.setUserId(1L);
        request.setPhotoUrl("http://example.com/rosemary.jpg");
        request.setCompanionId("munch");

        when(companionRepository.findById("munch")).thenReturn(Optional.of(munchCompanion));
        when(userService.getOrCreateDefaultUser()).thenReturn(testUser);

        GemmaVisionResult visionResult = new GemmaVisionResult(
                "Wild Rosemary", "Salvia rosmarinus", "food", 0.96, "Aromatic evergreen foliage"
        );
        when(gemmaService.identifyImage(any(), any(), any())).thenReturn(visionResult);

        SerpApiResult serpResult = new SerpApiResult(
                "Wild Rosemary",
                List.of("Secretes essential oils from glandular hairs."),
                List.of()
        );
        when(serpApiService.searchKnowledge(any(), any())).thenReturn(serpResult);

        CharacterDiscoveryOutput charOutput = new CharacterDiscoveryOutput(
                "Munch", "Food Culture", "Look at that aromatic marvel! 🫐",
                "Fascinating culinary roots.",
                "Brush foliage gently.",
                "⚠️ Foraging Safety Note: Never consume wild plants based solely on AI identification!",
                List.of("Secretes essential oils from glandular hairs.")
        );
        when(characterPersonalityService.formatDiscoveryOutput(munchCompanion, visionResult, serpResult))
                .thenReturn(charOutput);

        when(discoveryRepository.save(any(Discovery.class))).thenAnswer(i -> {
            Discovery d = i.getArgument(0);
            d.setId(600L);
            return d;
        });

        IdentifyPhotoResponse response = discoveryService.identifyPhoto(request);

        assertNotNull(response);
        assertNotNull(response.getSafetyDisclaimer());
        assertTrue(response.getSafetyDisclaimer().contains("Foraging Safety Note"));
    }

    @Test
    @DisplayName("Should handle SerpApi failure gracefully without failing identification")
    void testSerpApiFailureGracefulFallback() {
        IdentifyPhotoRequest request = new IdentifyPhotoRequest();
        request.setUserId(1L);
        request.setPhotoUrl("http://example.com/oak.jpg");
        request.setCompanionId("flora");

        when(companionRepository.findById("flora")).thenReturn(Optional.of(birdoCompanion));
        when(userService.getOrCreateDefaultUser()).thenReturn(testUser);

        GemmaVisionResult visionResult = new GemmaVisionResult(
                "English Oak", "Quercus robur", "plant", 0.96, "Lobed leaf with auricles"
        );
        when(gemmaService.identifyImage(any(), any(), any())).thenReturn(visionResult);

        // SerpApi returns empty fallback
        SerpApiResult emptySerp = new SerpApiResult("English Oak", List.of(), List.of());
        when(serpApiService.searchKnowledge("English Oak", "plant")).thenReturn(emptySerp);

        CharacterDiscoveryOutput charOutput = new CharacterDiscoveryOutput(
                "Flora", "Botany", "Wonderful oak specimen! 🌿",
                "Quercus robur", "Trace leaf veins.", null, List.of()
        );
        when(characterPersonalityService.formatDiscoveryOutput(any(), any(), any()))
                .thenReturn(charOutput);

        when(discoveryRepository.save(any(Discovery.class))).thenAnswer(i -> {
            Discovery d = i.getArgument(0);
            d.setId(700L);
            return d;
        });

        IdentifyPhotoResponse response = discoveryService.identifyPhoto(request);

        assertNotNull(response);
        assertTrue(response.isIdentified());
        assertEquals("English Oak", response.getIdentifiedTitle());
        verify(discoveryRepository, times(1)).save(any(Discovery.class));
    }
}
