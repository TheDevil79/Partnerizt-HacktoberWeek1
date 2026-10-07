package com.partnerizt.service;

import com.partnerizt.dto.CompleteQuestRequest;
import com.partnerizt.dto.QuestDto;
import com.partnerizt.model.Quest;

import java.util.List;

public interface QuestService {
    List<QuestDto> getAllQuests();
    List<QuestDto> getDailyQuests();
    QuestDto getQuestById(Long id);
    Quest getQuestEntityById(Long id);
    QuestDto startQuest(Long questId, Long userId);
    QuestDto completeQuest(Long questId, CompleteQuestRequest request);
}
