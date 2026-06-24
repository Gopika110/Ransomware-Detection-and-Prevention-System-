package com.ransomware.detection.service;

import com.ransomware.detection.model.*;
import com.ransomware.detection.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private FileLogRepository fileLogRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private ReportRepository reportRepository;

    // Generate a new report from current system state and save it
    public Report generateReport(String title) {
        long threatsCount = threatRepository.count();
        long alertsCount = alertRepository.count();
        long logsCount = fileLogRepository.count();

        long criticalThreats = threatRepository.findAll().stream()
                .filter(t -> "CRITICAL".equalsIgnoreCase(t.getLevel()))
                .count();

        long blockedThreats = threatRepository.countByStatus("BLOCKED");

        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Report generated on %s.\n", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        summary.append(String.format("Total File Activities Monitored: %d\n", logsCount));
        summary.append(String.format("Total Alerts Triggered: %d\n", alertsCount));
        summary.append(String.format("Total Threats Detected: %d (Critical: %d, Blocked: %d)\n\n", 
                threatsCount, criticalThreats, blockedThreats));
        
        if (threatsCount == 0) {
            summary.append("System status is SAFE. No threats have been detected in the monitoring scope.");
        } else {
            summary.append("WARNING: System has detected threat patterns. Review the Threat Detection logs for details.");
        }

        Report report = new Report();
        report.setTitle(title != null && !title.trim().isEmpty() ? title : "Security Activity Report");
        report.setThreatsCount((int) threatsCount);
        report.setAlertsCount((int) alertsCount);
        report.setLogsCount((int) logsCount);
        report.setSummary(summary.toString());

        return reportRepository.save(report);
    }

    // Export system activity logs and threats as CSV text
    public String getReportCsvData() {
        StringBuilder csv = new StringBuilder();
        csv.append("--- SYSTEM ACTIVITY REPORT ---\n");
        csv.append("Generated At,").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");

        // 1. Threats Section
        csv.append("THREATS SUMMARY\n");
        csv.append("ID,Threat Name,Severity,Detection Time,Status,Affected Files\n");
        List<Threat> threats = threatRepository.findAllByOrderByDetectionTimeDesc();
        for (Threat t : threats) {
            csv.append(String.format("%d,\"%s\",%s,%s,%s,\"%s\"\n",
                    t.getId(),
                    t.getName().replace("\"", "\"\""),
                    t.getLevel(),
                    t.getDetectionTime() != null ? t.getDetectionTime() : LocalDateTime.now(),
                    t.getStatus(),
                    t.getAffectedFiles() != null ? t.getAffectedFiles().replace("\"", "\"\"") : "None"
            ));
        }
        csv.append("\n");

        // 2. Alerts Section
        csv.append("SECURITY ALERTS\n");
        csv.append("ID,Message,Severity,Timestamp,Status\n");
        List<Alert> alerts = alertRepository.findAllByOrderByTimestampDesc();
        for (Alert a : alerts) {
            csv.append(String.format("%d,\"%s\",%s,%s,%s\n",
                    a.getId(),
                    a.getMessage().replace("\"", "\"\""),
                    a.getSeverity(),
                    a.getTimestamp() != null ? a.getTimestamp() : LocalDateTime.now(),
                    a.getIsRead() ? "Read" : "Unread"
            ));
        }
        csv.append("\n");

        // 3. File Activities Section
        csv.append("FILE MONITORING ACTIVITIES LOG\n");
        csv.append("ID,File Name,Path,Activity,Timestamp,Size (Bytes),Entropy,Suspicious\n");
        List<FileLog> logs = fileLogRepository.findAllByOrderByTimestampDesc();
        for (FileLog l : logs) {
            csv.append(String.format("%d,\"%s\",\"%s\",%s,%s,%d,%.4f,%b\n",
                    l.getId(),
                    l.getFileName().replace("\"", "\"\""),
                    l.getFilePath().replace("\"", "\"\""),
                    l.getActivityType(),
                    l.getTimestamp() != null ? l.getTimestamp() : LocalDateTime.now(),
                    l.getFileSize() != null ? l.getFileSize() : 0,
                    l.getEntropy() != null ? l.getEntropy() : 0.0,
                    l.getIsSuspicious() != null && l.getIsSuspicious()
            ));
        }

        return csv.toString();
    }
}
