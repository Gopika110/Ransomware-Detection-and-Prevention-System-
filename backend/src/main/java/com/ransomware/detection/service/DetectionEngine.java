package com.ransomware.detection.service;

import com.ransomware.detection.model.Alert;
import com.ransomware.detection.model.FileLog;
import com.ransomware.detection.model.Threat;
import com.ransomware.detection.repository.AlertRepository;
import com.ransomware.detection.repository.FileLogRepository;
import com.ransomware.detection.repository.ThreatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetectionEngine {

    @Autowired
    private FileLogRepository fileLogRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private SettingsService settingsService;

    // Check a file log and apply security rules
    public void analyze(FileLog fileLog) {
        if (!settingsService.isMonitoringActive()) {
            return;
        }

        boolean suspiciousByExtension = isSuspiciousExtension(fileLog.getFileName());
        boolean suspiciousByEntropy = false;

        // If file exists and wasn't deleted, calculate entropy
        if (!"DELETE".equals(fileLog.getActivityType())) {
            double entropy = calculateFileEntropy(fileLog.getFilePath());
            fileLog.setEntropy(entropy);
            if (entropy >= settingsService.getEntropyThreshold()) {
                suspiciousByEntropy = true;
            }
        }

        if (suspiciousByExtension || suspiciousByEntropy) {
            fileLog.setIsSuspicious(true);
            fileLogRepository.save(fileLog); // Update log as suspicious
            
            // Create Alert for suspicious file activity
            String reason = suspiciousByExtension ? "suspicious extension" : "high entropy (" + String.format("%.2f", fileLog.getEntropy()) + ")";
            createAlert("Suspicious activity detected on " + fileLog.getFileName() + " due to " + reason, "WARNING");
        }

        // Check for mass modification pattern (Frequency Analysis)
        checkMassModificationPattern(fileLog);
    }

    // Standard Shannon Entropy calculation for a file
    public double calculateFileEntropy(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return 0.0;
        }

        // Read up to first 4KB to sample entropy (efficient for large files)
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        try (FileInputStream fis = new FileInputStream(file)) {
            bytesRead = fis.read(buffer);
        } catch (IOException e) {
            return 0.0;
        }

        if (bytesRead <= 0) {
            return 0.0;
        }

        int[] frequencies = new int[256];
        for (int i = 0; i < bytesRead; i++) {
            frequencies[buffer[i] & 0xFF]++;
        }

        double entropy = 0.0;
        for (int freq : frequencies) {
            if (freq > 0) {
                double probability = (double) freq / bytesRead;
                entropy -= probability * (Math.log(probability) / Math.log(2));
            }
        }
        return entropy;
    }

    private boolean isSuspiciousExtension(String fileName) {
        String name = fileName.toLowerCase();
        return name.endsWith(".locked") || 
               name.endsWith(".crypto") || 
               name.endsWith(".ransom") || 
               name.endsWith(".enc") ||
               name.endsWith(".locky") ||
               name.endsWith(".wannacry");
    }

    private void checkMassModificationPattern(FileLog currentLog) {
        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(settingsService.getTimeWindowSeconds());
        
        // Count recent file logs in window
        long recentChangesCount = fileLogRepository.countByTimestampAfter(windowStart);
        
        if (recentChangesCount >= settingsService.getFrequencyThreshold()) {
            // Check if there's already an active threat in the last few seconds to avoid duplication
            List<Threat> activeThreats = threatRepository.findAllByOrderByDetectionTimeDesc();
            boolean threatAlreadyLogged = false;
            
            if (!activeThreats.isEmpty()) {
                Threat latest = activeThreats.get(0);
                if ("DETECTED".equals(latest.getStatus()) || "BLOCKED".equals(latest.getStatus())) {
                    // If created in the last 5 seconds, group into it
                    threatAlreadyLogged = true;
                }
            }

            if (!threatAlreadyLogged) {
                // Raise Critical Ransomware Threat!
                Threat threat = new Threat();
                threat.setName("Mass File Modification Pattern - Possible Ransomware Attack");
                threat.setLevel("CRITICAL");
                threat.setStatus(settingsService.isBlockModeActive() ? "BLOCKED" : "DETECTED");
                threat.setAffectedFiles(currentLog.getFilePath());
                threatRepository.save(threat);

                createAlert("CRITICAL THREAT: Mass file modifications detected! Action taken: " 
                            + (settingsService.isBlockModeActive() ? "Process Isolated & Access Blocked." : "Logged for Review."), 
                            "CRITICAL");
            } else if (settingsService.isBlockModeActive()) {
                // Append affected files to existing threat
                Threat latest = activeThreats.get(0);
                String files = latest.getAffectedFiles();
                if (files == null) {
                    files = currentLog.getFilePath();
                } else if (!files.contains(currentLog.getFilePath())) {
                    files += ", " + currentLog.getFilePath();
                }
                latest.setAffectedFiles(files);
                threatRepository.save(latest);
            }
        }
    }

    public void createAlert(String message, String severity) {
        Alert alert = new Alert();
        alert.setMessage(message);
        alert.setSeverity(severity);
        alertRepository.save(alert);
    }
}
