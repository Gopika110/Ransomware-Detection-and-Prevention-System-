package com.ransomware.detection.repository;

import com.ransomware.detection.model.Threat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ThreatRepository extends JpaRepository<Threat, Long> {
    List<Threat> findAllByOrderByDetectionTimeDesc();
    long countByStatus(String status);
}
