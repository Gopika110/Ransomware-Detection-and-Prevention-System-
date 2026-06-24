package com.ransomware.detection.repository;

import com.ransomware.detection.model.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FileLogRepository extends JpaRepository<FileLog, Long> {
    List<FileLog> findAllByOrderByTimestampDesc();
    long countByTimestampAfter(LocalDateTime timestamp);
    List<FileLog> findByIsSuspiciousTrueOrderByTimestampDesc();
}
