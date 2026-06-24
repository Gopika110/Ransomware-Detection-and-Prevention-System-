package com.ransomware.detection.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String message;

    @Column(nullable = false, length = 20)
    private String severity; // INFO, WARNING, CRITICAL

    @Column(insertable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private Boolean isRead = false;
}
