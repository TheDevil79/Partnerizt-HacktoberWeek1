package com.partnerizt.repository;

import com.partnerizt.model.Discovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscoveryRepository extends JpaRepository<Discovery, Long> {
    List<Discovery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Discovery> findBySessionId(Long sessionId);
    List<Discovery> findByCompanionId(String companionId);
    List<Discovery> findByUserIdAndCategory(Long userId, String category);
    long countByUserId(Long userId);
}
