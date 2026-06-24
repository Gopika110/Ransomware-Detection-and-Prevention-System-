package com.ransomware.detection.controller;

import com.ransomware.detection.model.Alert;
import com.ransomware.detection.model.FileLog;
import com.ransomware.detection.model.Threat;
import com.ransomware.detection.repository.AlertRepository;
import com.ransomware.detection.repository.FileLogRepository;
import com.ransomware.detection.repository.ThreatRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private FileLogRepository fileLogRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @GetMapping("/stats")
    public DashboardStats getStats() {
        long totalFiles = fileLogRepository.count();
        long threats = threatRepository.count();
        
        long blocked = threatRepository.findAll().stream()
                .filter(t -> "BLOCKED".equalsIgnoreCase(t.getStatus()))
                .count();

        // Get top 5 alerts
        List<Alert> recentAlerts = alertRepository.findAllByOrderByTimestampDesc().stream()
                .limit(5)
                .collect(Collectors.toList());

        // Get top 10 activities
        List<FileLog> recentActivities = fileLogRepository.findAllByOrderByTimestampDesc().stream()
                .limit(10)
                .collect(Collectors.toList());

        // Determine general status
        String status = "SAFE";
        List<Threat> activeThreats = threatRepository.findAll().stream()
                .filter(t -> !"RESOLVED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (!activeThreats.isEmpty()) {
            status = "THREAT DETECTED";
        } else {
            long warningAlerts = alertRepository.findAll().stream()
                    .filter(a -> "WARNING".equalsIgnoreCase(a.getSeverity()) && !a.getIsRead())
                    .count();
            if (warningAlerts > 0) {
                status = "WARNING ALERT";
            }
        }

        DashboardStats stats = new DashboardStats();
        stats.setStatus(status);
        stats.setTotalFilesMonitored(totalFiles);
        stats.setThreatsDetected(threats);
        stats.setThreatsBlocked(blocked);
        stats.setRecentAlerts(recentAlerts);
        stats.setRecentActivities(recentActivities);

        return stats;
    }

    @Data
    public static class DashboardStats {
        private String status;
        private long totalFilesMonitored;
        private long threatsDetected;
        private long threatsBlocked;
        private List<Alert> recentAlerts;
        private List<FileLog> recentActivities;
    }
}
