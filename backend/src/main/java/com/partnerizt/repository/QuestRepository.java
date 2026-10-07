package com.partnerizt.repository;

import com.partnerizt.model.Quest;
import com.partnerizt.model.QuestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByStatus(QuestStatus status);
    List<Quest> findByCompanionId(String companionId);
    List<Quest> findByIsDailyTrue();
    
    @Query("SELECT q FROM Quest q WHERE q.isDaily = true AND (q.status = 'AVAILABLE' OR q.status = 'ACTIVE')")
    List<Quest> findActiveDailyQuests();

    List<Quest> findByDomainIgnoreCase(String domain);
}
