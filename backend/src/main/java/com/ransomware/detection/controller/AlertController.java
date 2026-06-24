package com.ransomware.detection.controller;

import com.ransomware.detection.model.Alert;
import com.ransomware.detection.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAllByOrderByTimestampDesc();
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearAllAlerts() {
        alertRepository.deleteAll();
        Alert infoAlert = new Alert();
        infoAlert.setMessage("Security alert log was cleared by administrator.");
        infoAlert.setSeverity("INFO");
        alertRepository.save(infoAlert);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setIsRead(true);
            alertRepository.save(alert);
            return ResponseEntity.ok(alert);
        }
        return ResponseEntity.notFound().build();
    }
}
