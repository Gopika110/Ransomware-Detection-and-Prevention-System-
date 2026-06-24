package com.ransomware.detection.service;

import com.ransomware.detection.model.FileLog;
import com.ransomware.detection.repository.FileLogRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileMonitoringService {

    @Autowired
    private FileLogRepository fileLogRepository;

    @Autowired
    private DetectionEngine detectionEngine;

    @Autowired
    private SettingsService settingsService;

    private WatchService watchService;
    private ExecutorService executorService;
    private boolean running = false;
    private Path currentMonitoredPath;

    @PostConstruct
    public void start() {
        running = true;
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::monitorDirectory);
    }

    private void monitorDirectory() {
        try {
            String targetPathStr = settingsService.getMonitoredPath();
            currentMonitoredPath = Paths.get(targetPathStr).toAbsolutePath();
            
            // Ensure monitored directory exists
            Files.createDirectories(currentMonitoredPath);
            
            watchService = FileSystems.getDefault().newWatchService();
            currentMonitoredPath.register(watchService, 
                    StandardWatchEventKinds.ENTRY_CREATE, 
                    StandardWatchEventKinds.ENTRY_MODIFY, 
                    StandardWatchEventKinds.ENTRY_DELETE);
            
            while (running) {
                WatchKey key;
                try {
                    // Check if path was updated in settings
                    String currentSettingsPath = settingsService.getMonitoredPath();
                    Path absoluteSettingsPath = Paths.get(currentSettingsPath).toAbsolutePath();
                    if (!absoluteSettingsPath.equals(currentMonitoredPath)) {
                        // Restart watcher with new path
                        watchService.close();
                        currentMonitoredPath = absoluteSettingsPath;
                        Files.createDirectories(currentMonitoredPath);
                        watchService = FileSystems.getDefault().newWatchService();
                        currentMonitoredPath.register(watchService, 
                                StandardWatchEventKinds.ENTRY_CREATE, 
                                StandardWatchEventKinds.ENTRY_MODIFY, 
                                StandardWatchEventKinds.ENTRY_DELETE);
                    }
                    
                    key = watchService.take(); // Blocks until an event occurs
                } catch (ClosedWatchServiceException e) {
                    if (!running) break;
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    Path childPath = currentMonitoredPath.resolve(fileName);
                    
                    String activityType = "MODIFY";
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        activityType = "CREATE";
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        activityType = "DELETE";
                    }
                    
                    // Skip temporary/editor lock files if they cause noise
                    if (fileName.toString().endsWith("~") || fileName.toString().startsWith(".")) {
                        continue;
                    }
                    
                    FileLog fileLog = new FileLog();
                    fileLog.setFileName(fileName.toString());
                    fileLog.setFilePath(childPath.toString());
                    fileLog.setActivityType(activityType);
                    
                    File file = childPath.toFile();
                    if (file.exists() && !file.isDirectory()) {
                        fileLog.setFileSize(file.length());
                    } else {
                        fileLog.setFileSize(0L);
                    }
                    
                    // Save initial file log
                    fileLogRepository.save(fileLog);
                    
                    // Send to detection engine
                    detectionEngine.analyze(fileLog);
                }
                
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        try {
            if (watchService != null) {
                watchService.close();
            }
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
