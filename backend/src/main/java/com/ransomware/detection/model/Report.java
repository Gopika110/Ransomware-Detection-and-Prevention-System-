package com.ransomware.detection.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "generated_at", insertable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "threats_count")
    private Integer threatsCount = 0;

    @Column(name = "alerts_count")
    private Integer alertsCount = 0;

    @Column(name = "logs_count")
    private Integer logsCount = 0;

    @Column(columnDefinition = "TEXT")
    private String summary;
}
