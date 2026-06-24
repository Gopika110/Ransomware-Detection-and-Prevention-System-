package com.ransomware.detection.controller;

import com.ransomware.detection.model.FileLog;
import com.ransomware.detection.repository.FileLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {

    @Autowired
    private FileLogRepository fileLogRepository;

    @GetMapping("/activities")
    public List<FileLog> getAllActivities() {
        return fileLogRepository.findAllByOrderByTimestampDesc();
    }

    @GetMapping("/suspicious")
    public List<FileLog> getSuspiciousActivities() {
        return fileLogRepository.findByIsSuspiciousTrueOrderByTimestampDesc();
    }
}
