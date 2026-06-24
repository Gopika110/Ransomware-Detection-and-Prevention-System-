package com.ransomware.detection.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "file_logs")
public class FileLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    @Column(name = "activity_type", nullable = false, length = 20)
    private String activityType; // CREATE, MODIFY, DELETE

    @Column(insertable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "entropy")
    private Double entropy;

    @Column(name = "is_suspicious")
    private Boolean isSuspicious = false;
}
