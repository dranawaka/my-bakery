package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.Report;
import com.aurelius.tech.mybakery.model.Report.ReportType;
import com.aurelius.tech.mybakery.service.ReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling report-related endpoints.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * Constructor with dependencies.
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    /**
     * Get all reports.
     *
     * @return a response entity with all reports
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(ApiResponse.success(reports, "Reports retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a report by ID.
     *
     * @param id the report ID
     * @return a response entity with the report
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getReportById(@PathVariable Long id) {
        try {
            Report report = reportService.getReportById(id);
            return ResponseEntity.ok(ApiResponse.success(report, "Report retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get reports by type.
     *
     * @param type the report type
     * @return a response entity with the reports
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<?>> getReportsByType(@PathVariable ReportType type) {
        try {
            List<Report> reports = reportService.getReportsByType(type);
            return ResponseEntity.ok(ApiResponse.success(reports, "Reports retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get reports by creator.
     *
     * @param userId the user ID of the creator
     * @return a response entity with the reports
     */
    @GetMapping("/creator/{userId}")
    public ResponseEntity<ApiResponse<?>> getReportsByCreator(@PathVariable Long userId) {
        try {
            List<Report> reports = reportService.getReportsByCreator(userId);
            return ResponseEntity.ok(ApiResponse.success(reports, "Reports retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get reports by date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a response entity with the reports
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<?>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Report> reports = reportService.getReportsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(reports, "Reports retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate a sales report.
     *
     * @param requestBody the request body containing the report parameters
     * @return a response entity with the generated report
     */
    @PostMapping("/sales")
    public ResponseEntity<ApiResponse<?>> generateSalesReport(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String fileType = (String) requestBody.get("fileType");
            
            Report report = reportService.generateSalesReport(startDate, endDate, userId, fileType);
            return ResponseEntity.ok(ApiResponse.success(report, "Sales report generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate an inventory report.
     *
     * @param requestBody the request body containing the report parameters
     * @return a response entity with the generated report
     */
    @PostMapping("/inventory")
    public ResponseEntity<ApiResponse<?>> generateInventoryReport(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String fileType = (String) requestBody.get("fileType");
            
            Report report = reportService.generateInventoryReport(userId, fileType);
            return ResponseEntity.ok(ApiResponse.success(report, "Inventory report generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate a customer report.
     *
     * @param requestBody the request body containing the report parameters
     * @return a response entity with the generated report
     */
    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<?>> generateCustomerReport(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String fileType = (String) requestBody.get("fileType");
            
            Report report = reportService.generateCustomerReport(startDate, endDate, userId, fileType);
            return ResponseEntity.ok(ApiResponse.success(report, "Customer report generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate a product report.
     *
     * @param requestBody the request body containing the report parameters
     * @return a response entity with the generated report
     */
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> generateProductReport(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String fileType = (String) requestBody.get("fileType");
            
            Report report = reportService.generateProductReport(startDate, endDate, userId, fileType);
            return ResponseEntity.ok(ApiResponse.success(report, "Product report generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate a financial report.
     *
     * @param requestBody the request body containing the report parameters
     * @return a response entity with the generated report
     */
    @PostMapping("/financial")
    public ResponseEntity<ApiResponse<?>> generateFinancialReport(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String fileType = (String) requestBody.get("fileType");
            
            Report report = reportService.generateFinancialReport(startDate, endDate, userId, fileType);
            return ResponseEntity.ok(ApiResponse.success(report, "Financial report generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a report.
     *
     * @param id the report ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Report deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}