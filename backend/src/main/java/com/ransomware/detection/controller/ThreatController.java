package com.ransomware.detection.controller;

import com.ransomware.detection.model.Threat;
import com.ransomware.detection.repository.ThreatRepository;
import com.ransomware.detection.service.DetectionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/threats")
@CrossOrigin(origins = "*")
public class ThreatController {

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private DetectionEngine detectionEngine;

    @GetMapping
    public List<Threat> getAllThreats() {
        return threatRepository.findAllByOrderByDetectionTimeDesc();
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveThreat(@PathVariable Long id) {
        Optional<Threat> threatOpt = threatRepository.findById(id);
        if (threatOpt.isPresent()) {
            Threat threat = threatOpt.get();
            threat.setStatus("RESOLVED");
            threatRepository.save(threat);
            detectionEngine.createAlert("Threat (ID: " + id + ") has been resolved by administrator.", "INFO");
            return ResponseEntity.ok(threat);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockThreat(@PathVariable Long id) {
        Optional<Threat> threatOpt = threatRepository.findById(id);
        if (threatOpt.isPresent()) {
            Threat threat = threatOpt.get();
            threat.setStatus("BLOCKED");
            threatRepository.save(threat);
            detectionEngine.createAlert("Threat (ID: " + id + ") has been blocked & isolated by administrator.", "WARNING");
            return ResponseEntity.ok(threat);
        }
        return ResponseEntity.notFound().build();
    }
}
