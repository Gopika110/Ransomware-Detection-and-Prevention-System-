package com.ransomware.detection.service;

import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private String monitoredPath = "./monitored_directory";
    private double entropyThreshold = 7.0;
    private int frequencyThreshold = 8; // Number of modifications in window
    private int timeWindowSeconds = 5;
    private boolean monitoringActive = true;
    private boolean blockModeActive = true;

    public String getMonitoredPath() {
        return monitoredPath;
    }

    public void setMonitoredPath(String monitoredPath) {
        this.monitoredPath = monitoredPath;
    }

    public double getEntropyThreshold() {
        return entropyThreshold;
    }

    public void setEntropyThreshold(double entropyThreshold) {
        this.entropyThreshold = entropyThreshold;
    }

    public int getFrequencyThreshold() {
        return frequencyThreshold;
    }

    public void setFrequencyThreshold(int frequencyThreshold) {
        this.frequencyThreshold = frequencyThreshold;
    }

    public int getTimeWindowSeconds() {
        return timeWindowSeconds;
    }

    public void setTimeWindowSeconds(int timeWindowSeconds) {
        this.timeWindowSeconds = timeWindowSeconds;
    }

    public boolean isMonitoringActive() {
        return monitoringActive;
    }

    public void setMonitoringActive(boolean monitoringActive) {
        this.monitoringActive = monitoringActive;
    }

    public boolean isBlockModeActive() {
        return blockModeActive;
    }

    public void setBlockModeActive(boolean blockModeActive) {
        this.blockModeActive = blockModeActive;
    }
}
