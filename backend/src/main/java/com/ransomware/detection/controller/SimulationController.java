package com.ransomware.detection.controller;

import com.ransomware.detection.model.*;
import com.ransomware.detection.repository.*;
import com.ransomware.detection.service.DetectionEngine;
import com.ransomware.detection.service.SettingsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    @Autowired
    private FileLogRepository fileLogRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private DetectionEngine detectionEngine;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SecureRandom random = new SecureRandom();

    @PostMapping("/start")
    public ResponseEntity<?> startSimulation() {
        executor.submit(() -> {
            try {
                runAttackSimulation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(new SimResponse("Simulation started asynchronously. Watch the dashboard for live telemetry!"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetSimulation() {
        try {
            performSystemReset();
            return ResponseEntity.ok(new SimResponse("Workspace and database successfully reset to clean seed baseline."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new SimResponse("Failed to reset: " + e.getMessage()));
        }
    }

    private void runAttackSimulation() throws IOException, InterruptedException {
        String monitoredFolder = settingsService.getMonitoredPath();
        File dir = new File(monitoredFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        detectionEngine.createAlert("ALERT: Ransomware simulation initialized by administrator. Attack vector deploying...", "WARNING");
        Thread.sleep(1000);

        int filesToModify = 12;
        File[] simulatedFiles = new File[filesToModify];

        // Phase 1: Rapidly create harmless text files
        for (int i = 0; i < filesToModify; i++) {
            simulatedFiles[i] = new File(dir, "simulated_user_file_" + i + ".txt");
            try (FileOutputStream fos = new FileOutputStream(simulatedFiles[i])) {
                String content = "This is a normal user file with standard plain text metadata contents. File number " + i;
                fos.write(content.getBytes());
            }
            Thread.sleep(150); // Pause briefly between creations
        }

        Thread.sleep(1000);

        // Phase 2: Rapidly encrypt files (write high entropy bytes) and rename to .locked
        for (int i = 0; i < filesToModify; i++) {
            File currentFile = simulatedFiles[i];
            if (currentFile.exists()) {
                // Generate high entropy random bytes to simulate AES encryption
                byte[] cipherBytes = new byte[1024];
                random.nextBytes(cipherBytes);

                // Write encrypted content
                try (FileOutputStream fos = new FileOutputStream(currentFile)) {
                    fos.write(cipherBytes);
                }

                // Simulate renaming to locked extension
                File lockedFile = new File(dir, currentFile.getName().replace(".txt", ".locked"));
                if (currentFile.renameTo(lockedFile)) {
                    simulatedFiles[i] = lockedFile;
                }
                
                Thread.sleep(100); // Trigger frequency check (multiple modifications in seconds)
            }
        }
    }

    private void performSystemReset() {
        // 1. Delete simulated files from folder
        String folderPath = settingsService.getMonitoredPath();
        File dir = new File(folderPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().contains("simulated") || f.getName().endsWith(".locked") || f.getName().endsWith(".crypto")) {
                        f.delete();
                    }
                }
            }
        }

        // 2. Clear Database Tables
        fileLogRepository.deleteAll();
        alertRepository.deleteAll();
        threatRepository.deleteAll();
        reportRepository.deleteAll();

        // 3. Re-insert baseline seed data
        // Insert logs
        saveLog("document1.docx", "C:\\monitored_directory\\document1.docx", "CREATE", 15420L, 4.25, false);
        saveLog("photo1.jpg", "C:\\monitored_directory\\photo1.jpg", "CREATE", 245000L, 7.82, false);
        saveLog("script.py", "C:\\monitored_directory\\script.py", "MODIFY", 1240L, 3.41, false);
        saveLog("system_log.txt", "C:\\monitored_directory\\system_log.txt", "DELETE", 520L, 2.15, false);
        saveLog("invoice_draft.pdf", "C:\\monitored_directory\\invoice_draft.pdf", "CREATE", 87400L, 5.12, false);

        // Insert Alerts
        saveAlert("System file monitoring service initialized.", "INFO");
        saveAlert("Scanner detected high entropy file: photo1.jpg (entropy: 7.82). Categorized as safe media.", "INFO");
        saveAlert("Directory watch service started monitoring ./monitored_directory", "INFO");

        // Insert Threat
        Threat threat = new Threat();
        threat.setName("Mock Ransomware Pattern Detector");
        threat.setLevel("LOW");
        threat.setStatus("RESOLVED");
        threat.setAffectedFiles("C:\\monitored_directory\\test_file.locked");
        threatRepository.save(threat);

        // Insert Report
        Report report = new Report();
        report.setTitle("System Security Report - Initial Baseline");
        report.setThreatsCount(1);
        report.setAlertsCount(3);
        report.setLogsCount(5);
        report.setSummary("Initial baseline system health check report. Monitored directory was set up and verified. System status is safe with zero active threats.");
        reportRepository.save(report);
    }

    private void saveLog(String name, String path, String type, long size, double entropy, boolean suspicious) {
        FileLog log = new FileLog();
        log.setFileName(name);
        log.setFilePath(path);
        log.setActivityType(type);
        log.setFileSize(size);
        log.setEntropy(entropy);
        log.setIsSuspicious(suspicious);
        fileLogRepository.save(log);
    }

    private void saveAlert(String msg, String severity) {
        Alert a = new Alert();
        a.setMessage(msg);
        a.setSeverity(severity);
        alertRepository.save(a);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimResponse {
        private String message;
    }
}
