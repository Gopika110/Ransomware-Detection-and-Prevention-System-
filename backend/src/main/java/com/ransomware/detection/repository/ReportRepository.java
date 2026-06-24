package com.ransomware.detection.repository;

import com.ransomware.detection.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOrderByGeneratedAtDesc();
}
