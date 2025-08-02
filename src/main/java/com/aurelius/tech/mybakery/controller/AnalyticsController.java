package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.Analytics;
import com.aurelius.tech.mybakery.model.Analytics.AnalyticsType;
import com.aurelius.tech.mybakery.service.AnalyticsService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling analytics-related endpoints.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Constructor with dependencies.
     */
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    
    /**
     * Get all analytics.
     *
     * @return a response entity with all analytics
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllAnalytics() {
        try {
            List<Analytics> analyticsList = analyticsService.getAllAnalytics();
            return ResponseEntity.ok(ApiResponse.success(analyticsList, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get analytics by ID.
     *
     * @param id the analytics ID
     * @return a response entity with the analytics
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getAnalyticsById(@PathVariable Long id) {
        try {
            Analytics analytics = analyticsService.getAnalyticsById(id);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get analytics by type.
     *
     * @param type the analytics type
     * @return a response entity with the analytics
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<?>> getAnalyticsByType(@PathVariable AnalyticsType type) {
        try {
            List<Analytics> analyticsList = analyticsService.getAnalyticsByType(type);
            return ResponseEntity.ok(ApiResponse.success(analyticsList, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get analytics by time period.
     *
     * @param timePeriod the time period
     * @return a response entity with the analytics
     */
    @GetMapping("/time-period/{timePeriod}")
    public ResponseEntity<ApiResponse<?>> getAnalyticsByTimePeriod(@PathVariable String timePeriod) {
        try {
            List<Analytics> analyticsList = analyticsService.getAnalyticsByTimePeriod(timePeriod);
            return ResponseEntity.ok(ApiResponse.success(analyticsList, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the most recent analytics by type.
     *
     * @param type the analytics type
     * @return a response entity with the analytics
     */
    @GetMapping("/recent/{type}")
    public ResponseEntity<ApiResponse<?>> getMostRecentAnalyticsByType(@PathVariable AnalyticsType type) {
        try {
            List<Analytics> analyticsList = analyticsService.getMostRecentAnalyticsByType(type);
            return ResponseEntity.ok(ApiResponse.success(analyticsList, "Analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get dashboard analytics.
     *
     * @return a response entity with the dashboard analytics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> getDashboardAnalytics() {
        try {
            Analytics analytics = analyticsService.generateDashboardAnalytics();
            return ResponseEntity.ok(ApiResponse.success(analytics, "Dashboard analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get dashboard summary data.
     *
     * @return a response entity with dashboard summary
     */
    @GetMapping("/dashboard-summary")
    public ResponseEntity<ApiResponse<?>> getDashboardSummary() {
        try {
            Map<String, Object> summaryData = analyticsService.getDashboardSummary();
            return ResponseEntity.ok(ApiResponse.success(summaryData, "Dashboard summary retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate sales trend analytics.
     *
     * @param requestBody the request body containing the analytics parameters
     * @return a response entity with the generated analytics
     */
    @PostMapping("/sales-trend")
    public ResponseEntity<ApiResponse<?>> generateSalesTrendAnalytics(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            String timePeriod = (String) requestBody.get("timePeriod");
            
            Analytics analytics = analyticsService.generateSalesTrendAnalytics(startDate, endDate, timePeriod);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Sales trend analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate inventory level analytics.
     *
     * @return a response entity with the generated analytics
     */
    @PostMapping("/inventory-level")
    public ResponseEntity<ApiResponse<?>> generateInventoryLevelAnalytics() {
        try {
            Analytics analytics = analyticsService.generateInventoryLevelAnalytics();
            return ResponseEntity.ok(ApiResponse.success(analytics, "Inventory level analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate customer behavior analytics.
     *
     * @param requestBody the request body containing the analytics parameters
     * @return a response entity with the generated analytics
     */
    @PostMapping("/customer-behavior")
    public ResponseEntity<ApiResponse<?>> generateCustomerBehaviorAnalytics(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            
            Analytics analytics = analyticsService.generateCustomerBehaviorAnalytics(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Customer behavior analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate product performance analytics.
     *
     * @param requestBody the request body containing the analytics parameters
     * @return a response entity with the generated analytics
     */
    @PostMapping("/product-performance")
    public ResponseEntity<ApiResponse<?>> generateProductPerformanceAnalytics(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            
            Analytics analytics = analyticsService.generateProductPerformanceAnalytics(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Product performance analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Generate financial metrics analytics.
     *
     * @param requestBody the request body containing the analytics parameters
     * @return a response entity with the generated analytics
     */
    @PostMapping("/financial-metrics")
    public ResponseEntity<ApiResponse<?>> generateFinancialMetricsAnalytics(@RequestBody Map<String, Object> requestBody) {
        try {
            LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
            
            Analytics analytics = analyticsService.generateFinancialMetricsAnalytics(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Financial metrics analytics generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get analytics trends.
     *
     * @param type the analytics type
     * @param startDate the start date
     * @param endDate the end date
     * @return a response entity with the analytics trends
     */
    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<?>> getAnalyticsTrends(
            @RequestParam AnalyticsType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // In a real implementation, we would have a method to get analytics trends
            // For now, we'll just return the analytics for the specified type and date range
            List<Analytics> analyticsList = analyticsService.getAnalyticsByType(type);
            return ResponseEntity.ok(ApiResponse.success(analyticsList, "Analytics trends retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete analytics.
     *
     * @param id the analytics ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAnalytics(@PathVariable Long id) {
        try {
            analyticsService.deleteAnalytics(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Analytics deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}