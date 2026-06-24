package com.ransomware.detection.repository;

import com.ransomware.detection.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByOrderByTimestampDesc();
}
