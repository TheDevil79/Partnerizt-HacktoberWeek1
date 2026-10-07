package com.partnerizt.service;

import com.partnerizt.dto.CompleteQuestRequest;
import com.partnerizt.dto.QuestDto;
import com.partnerizt.exception.BadRequestException;
import com.partnerizt.exception.ResourceNotFoundException;
import com.partnerizt.model.*;
import com.partnerizt.repository.DiscoveryRepository;
import com.partnerizt.repository.QuestRepository;
import com.partnerizt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final DiscoveryRepository discoveryRepository;
    private final UserService userService;

    public QuestServiceImpl(QuestRepository questRepository, UserRepository userRepository,
                            DiscoveryRepository discoveryRepository, UserService userService) {
        this.questRepository = questRepository;
        this.userRepository = userRepository;
        this.discoveryRepository = discoveryRepository;
        this.userService = userService;
    }

    @Override
    public List<QuestDto> getAllQuests() {
        return questRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestDto> getDailyQuests() {
        List<Quest> dailyQuests = questRepository.findActiveDailyQuests();
        if (dailyQuests.isEmpty()) {
            dailyQuests = questRepository.findByIsDailyTrue();
        }
        return dailyQuests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuestDto getQuestById(Long id) {
        Quest quest = getQuestEntityById(id);
        return mapToDto(quest);
    }

    @Override
    public Quest getQuestEntityById(Long id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quest", "id", id));
    }

    @Override
    @Transactional
    public QuestDto startQuest(Long questId, Long userId) {
        Quest quest = getQuestEntityById(questId);
        if (quest.getStatus() == QuestStatus.COMPLETED) {
            throw new BadRequestException("Quest has already been completed");
        }
        quest.setStatus(QuestStatus.ACTIVE);
        Quest saved = questRepository.save(quest);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public QuestDto completeQuest(Long questId, CompleteQuestRequest request) {
        Quest quest = getQuestEntityById(questId);

        // Duplicate quest completion prevention
        if (quest.getStatus() == QuestStatus.COMPLETED) {
            throw new BadRequestException("Quest has already been completed");
        }

        quest.setStatus(QuestStatus.COMPLETED);
        Quest saved = questRepository.save(quest);

        Long userId = (request != null && request.getUserId() != null)
                ? request.getUserId()
                : userService.getOrCreateDefaultUser().getId();

        // Award XP and coins to user
        userService.addXpAndCoins(userId, quest.getXpReward(), quest.getCoinReward());

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setQuestsCompletedCount(user.getQuestsCompletedCount() + 1);
            userRepository.save(user);
        }

        // If photo or notes provided, save verified discovery
        if (request != null && (request.getPhotoUrl() != null || request.getNotes() != null)) {
            Discovery discovery = new Discovery(
                    userId,
                    null,
                    quest.getId(),
                    quest.getCompanionId(),
                    quest.getTitle() + " Target",
                    quest.getDomain(),
                    request.getNotes() != null ? request.getNotes() : quest.getDescription(),
                    request.getPhotoUrl(),
                    "Field verified during quest: " + quest.getTitle(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getLocationName() != null ? request.getLocationName() : quest.getTargetLocation(),
                    DiscoveryRarity.UNCOMMON,
                    quest.getXpReward() / 2
            );
            discoveryRepository.save(discovery);
            if (user != null) {
                user.setDiscoveriesCount(user.getDiscoveriesCount() + 1);
                userRepository.save(user);
            }
        }

        return mapToDto(saved);
    }

    public QuestDto mapToDto(Quest q) {
        QuestDto dto = new QuestDto();
        dto.setId(q.getId());
        dto.setTitle(q.getTitle());
        dto.setDescription(q.getDescription());
        dto.setDomain(q.getDomain());
        dto.setXpReward(q.getXpReward());
        dto.setCoinReward(q.getCoinReward());
        dto.setTargetDistance(q.getTargetDistance());
        dto.setTimeLimitMinutes(q.getTimeLimitMinutes());
        dto.setCompanionId(q.getCompanionId());
        dto.setStatus(q.getStatus());
        dto.setRequiresPhoto(q.getRequiresPhoto());
        dto.setPhotoTargetDescription(q.getPhotoTargetDescription());
        dto.setTargetLocation(q.getTargetLocation());
        dto.setDifficulty(q.getDifficulty());
        dto.setIsDaily(q.getIsDaily());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setExpiresAt(q.getExpiresAt());

        if (q.getExpiresAt() != null) {
            long remaining = Duration.between(LocalDateTime.now(), q.getExpiresAt()).getSeconds();
            dto.setRemainingSeconds(Math.max(0, remaining));
        } else {
            dto.setRemainingSeconds(3600L);
        }

        return dto;
    }
}
