package com.ransomware.detection.controller;

import com.ransomware.detection.service.SettingsService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public SettingsDto getSettings() {
        SettingsDto dto = new SettingsDto();
        dto.setMonitoredPath(settingsService.getMonitoredPath());
        dto.setEntropyThreshold(settingsService.getEntropyThreshold());
        dto.setFrequencyThreshold(settingsService.getFrequencyThreshold());
        dto.setTimeWindowSeconds(settingsService.getTimeWindowSeconds());
        dto.setMonitoringActive(settingsService.isMonitoringActive());
        dto.setBlockModeActive(settingsService.isBlockModeActive());
        return dto;
    }

    @PostMapping
    public SettingsDto updateSettings(@RequestBody SettingsDto dto) {
        if (dto.getMonitoredPath() != null) {
            settingsService.setMonitoredPath(dto.getMonitoredPath());
        }
        settingsService.setEntropyThreshold(dto.getEntropyThreshold());
        settingsService.setFrequencyThreshold(dto.getFrequencyThreshold());
        settingsService.setTimeWindowSeconds(dto.getTimeWindowSeconds());
        settingsService.setMonitoringActive(dto.isMonitoringActive());
        settingsService.setBlockModeActive(dto.isBlockModeActive());
        return getSettings();
    }

    @Data
    public static class SettingsDto {
        private String monitoredPath;
        private double entropyThreshold;
        private int frequencyThreshold;
        private int timeWindowSeconds;
        private boolean monitoringActive;
        private boolean blockModeActive;
    }
}
