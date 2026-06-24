package com.ransomware.detection.controller;

import com.ransomware.detection.model.Report;
import com.ransomware.detection.repository.ReportRepository;
import com.ransomware.detection.service.ReportService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByGeneratedAtDesc();
    }

    @PostMapping("/generate")
    public Report generateReport(@RequestBody ReportRequest request) {
        return reportService.generateReport(request.getTitle());
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadReportCsv() {
        String csvData = reportService.getReportCsvData();
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ransomware_security_report.csv");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportRequest {
        private String title;
    }
}
