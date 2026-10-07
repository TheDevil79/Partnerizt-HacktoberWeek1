package com.partnerizt.repository;

import com.partnerizt.model.ExplorationSession;
import com.partnerizt.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExplorationSessionRepository extends JpaRepository<ExplorationSession, Long> {
    List<ExplorationSession> findByUserIdOrderByStartTimeDesc(Long userId);
    Optional<ExplorationSession> findFirstByUserIdAndStatusOrderByStartTimeDesc(Long userId, SessionStatus status);
    List<ExplorationSession> findByUserIdAndStatus(Long userId, SessionStatus status);
}
