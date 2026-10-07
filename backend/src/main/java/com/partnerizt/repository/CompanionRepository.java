package com.partnerizt.repository;

import com.partnerizt.model.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanionRepository extends JpaRepository<Companion, String> {
    List<Companion> findByDomainContainingIgnoreCase(String domain);
}
