package com.partnerizt.controller;

import com.partnerizt.dto.ApiResponse;
import com.partnerizt.dto.CompleteQuestRequest;
import com.partnerizt.dto.QuestDto;
import com.partnerizt.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quests")
public class QuestController {

    private final QuestService questService;

    public QuestController(QuestService questService) {
        this.questService = questService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestDto>>> getAllQuests() {
        List<QuestDto> quests = questService.getAllQuests();
        return ResponseEntity.ok(ApiResponse.success(quests));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<QuestDto>>> getDailyQuests() {
        List<QuestDto> daily = questService.getDailyQuests();
        return ResponseEntity.ok(ApiResponse.success(daily));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestDto>> getQuestById(@PathVariable Long id) {
        QuestDto quest = questService.getQuestById(id);
        return ResponseEntity.ok(ApiResponse.success(quest));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<QuestDto>> startQuest(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        QuestDto quest = questService.startQuest(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Quest started! Time to head outdoors.", quest));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<QuestDto>> completeQuest(@PathVariable Long id, @RequestBody(required = false) CompleteQuestRequest request) {
        QuestDto quest = questService.completeQuest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Quest successfully completed! Rewards awarded.", quest));
    }
}
