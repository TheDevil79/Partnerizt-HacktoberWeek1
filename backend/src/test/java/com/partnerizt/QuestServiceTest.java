package com.partnerizt;

import com.partnerizt.dto.CompleteQuestRequest;
import com.partnerizt.dto.QuestDto;
import com.partnerizt.exception.BadRequestException;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.Discovery;
import com.partnerizt.model.Quest;
import com.partnerizt.model.QuestStatus;
import com.partnerizt.model.User;
import com.partnerizt.repository.DiscoveryRepository;
import com.partnerizt.repository.QuestRepository;
import com.partnerizt.repository.UserRepository;
import com.partnerizt.service.QuestServiceImpl;
import com.partnerizt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscoveryRepository discoveryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private QuestServiceImpl questService;

    private Quest testQuest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("nature_scout", "explorer@partnerizt.app");
        testUser.setId(1L);
        testUser.setXp(100);
        testUser.setCoins(50);
        testUser.setQuestsCompletedCount(0);

        testQuest = new Quest(
                "Find 3 Distinct Leaf Venation Patterns",
                "Photograph 3 leaves",
                "Botany & Dendrology",
                120,
                30,
                800.0,
                45,
                "flora",
                true,
                "Close-up photo of leaf venation",
                "Local Park",
                true,
                LocalDateTime.now().plusHours(8)
        );
        testQuest.setId(101L);
        testQuest.setStatus(QuestStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should successfully start an available quest")
    void testStartQuest() {
        when(questRepository.findById(101L)).thenReturn(Optional.of(testQuest));
        when(questRepository.save(any(Quest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuestDto result = questService.startQuest(101L, 1L);

        assertNotNull(result);
        assertEquals(QuestStatus.ACTIVE, result.getStatus());
        verify(questRepository, times(1)).save(testQuest);
    }

    @Test
    @DisplayName("Should successfully complete a quest and award XP/coins")
    void testCompleteQuestSuccess() {
        when(questRepository.findById(101L)).thenReturn(Optional.of(testQuest));
        when(questRepository.save(any(Quest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        CompleteQuestRequest request = new CompleteQuestRequest();
        request.setUserId(1L);
        request.setPhotoUrl("http://example.com/leaf.jpg");
        request.setNotes("Found pinnate leaf");

        QuestDto result = questService.completeQuest(101L, request);

        assertNotNull(result);
        assertEquals(QuestStatus.COMPLETED, result.getStatus());
        verify(userService, times(1)).addXpAndCoins(1L, 120, 30);
        verify(discoveryRepository, times(1)).save(any(Discovery.class));
        assertEquals(1, testUser.getQuestsCompletedCount());
    }

    @Test
    @DisplayName("Should throw BadRequestException when attempting duplicate quest completion")
    void testDuplicateQuestCompletionPrevention() {
        testQuest.setStatus(QuestStatus.COMPLETED);
        when(questRepository.findById(101L)).thenReturn(Optional.of(testQuest));

        CompleteQuestRequest request = new CompleteQuestRequest();
        request.setUserId(1L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            questService.completeQuest(101L, request);
        });

        assertEquals("Quest has already been completed", ex.getMessage());
        verify(userService, never()).addXpAndCoins(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when quest does not exist")
    void testMissingQuestHandling() {
        when(questRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            questService.getQuestById(999L);
        });
    }
}
