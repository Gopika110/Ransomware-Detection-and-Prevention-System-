package com.ransomware.detection.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "threats")
public class Threat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String level; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "detection_time", insertable = false, updatable = false)
    private LocalDateTime detectionTime;

    @Column(nullable = false, length = 20)
    private String status; // DETECTED, BLOCKED, RESOLVED

    @Column(name = "affected_files", columnDefinition = "TEXT")
    private String affectedFiles;
}
