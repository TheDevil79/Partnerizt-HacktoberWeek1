package com.partnerizt.repository;

import com.partnerizt.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdAndCompanionIdOrderByTimestampAsc(Long userId, String companionId);
    List<ChatMessage> findByUserIdOrderByTimestampDesc(Long userId);
    void deleteByUserIdAndCompanionId(Long userId, String companionId);
}
