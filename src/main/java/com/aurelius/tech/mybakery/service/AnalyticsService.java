package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Analytics;
import com.aurelius.tech.mybakery.model.Analytics.AnalyticsType;
import com.aurelius.tech.mybakery.repository.AnalyticsRepository;
import com.aurelius.tech.mybakery.repository.OrderRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;
import com.aurelius.tech.mybakery.repository.InventoryRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Service class for handling analytics operations.
 */
@Service
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependencies.
     */
    public AnalyticsService(
            AnalyticsRepository analyticsRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository) {
        this.analyticsRepository = analyticsRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Get analytics by ID.
     *
     * @param id the analytics ID
     * @return the analytics with the specified ID
     * @throws RuntimeException if the analytics is not found
     */
    public Analytics getAnalyticsById(Long id) {
        return analyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics not found with ID: " + id));
    }
    
    /**
     * Get all analytics.
     *
     * @return a list of all analytics
     */
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }
    
    /**
     * Get analytics by type.
     *
     * @param type the analytics type
     * @return a list of analytics with the specified type
     */
    public List<Analytics> getAnalyticsByType(AnalyticsType type) {
        return analyticsRepository.findByType(type);
    }
    
    /**
     * Get analytics by time period.
     *
     * @param timePeriod the time period
     * @return a list of analytics with the specified time period
     */
    public List<Analytics> getAnalyticsByTimePeriod(String timePeriod) {
        return analyticsRepository.findByTimePeriod(timePeriod);
    }
    
    /**
     * Get the most recent analytics by type.
     *
     * @param type the analytics type
     * @return a list of the most recent analytics with the specified type
     */
    public List<Analytics> getMostRecentAnalyticsByType(AnalyticsType type) {
        return analyticsRepository.findTop5ByTypeOrderByCreatedAtDesc(type);
    }
    
    /**
     * Generate sales trend analytics.
     *
     * @param startDate the start date for the analytics period
     * @param endDate the end date for the analytics period
     * @param timePeriod the time period (DAILY, WEEKLY, MONTHLY, etc.)
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateSalesTrendAnalytics(LocalDateTime startDate, LocalDateTime endDate, String timePeriod) {
        Analytics analytics = new Analytics();
        analytics.setName("Sales Trend - " + timePeriod);
        analytics.setDescription("Sales trend analysis for the period from " + formatDate(startDate) + " to " + formatDate(endDate));
        analytics.setType(AnalyticsType.SALES_TREND);
        analytics.setTimePeriod(timePeriod);
        analytics.setStartDate(startDate);
        analytics.setEndDate(endDate);
        
        // In a real implementation, we would calculate the total sales for the period
        // For now, we'll just use a sample value
        BigDecimal totalSales = new BigDecimal("2500.50");
        analytics.setNumericValue(totalSales);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("totalOrders", "125");
        data.put("averageOrderValue", "20.00");
        data.put("topSellingProduct", "Chocolate Cake");
        data.put("topSellingCategory", "Cakes");
        data.put("salesGrowth", "15.5%");
        
        // Add daily sales data for the period
        LocalDateTime currentDate = startDate;
        Random random = new Random();
        while (!currentDate.isAfter(endDate)) {
            String dateKey = "sales_" + currentDate.format(DateTimeFormatter.ISO_DATE);
            BigDecimal dailySales = new BigDecimal(random.nextInt(500) + 100)
                    .setScale(2, RoundingMode.HALF_UP);
            data.put(dateKey, dailySales.toString());
            currentDate = currentDate.plusDays(1);
        }
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Generate inventory level analytics.
     *
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateInventoryLevelAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        
        Analytics analytics = new Analytics();
        analytics.setName("Inventory Levels - Current");
        analytics.setDescription("Current inventory levels analysis");
        analytics.setType(AnalyticsType.INVENTORY_LEVEL);
        analytics.setTimePeriod("CURRENT");
        analytics.setStartDate(now);
        analytics.setEndDate(now);
        
        // In a real implementation, we would calculate the total inventory value
        // For now, we'll just use a sample value
        BigDecimal totalValue = new BigDecimal("12500.75");
        analytics.setNumericValue(totalValue);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("totalProducts", "50");
        data.put("inStockProducts", "35");
        data.put("lowStockProducts", "10");
        data.put("outOfStockProducts", "5");
        data.put("averageStockLevel", "22");
        data.put("mostStockedCategory", "Cakes");
        data.put("leastStockedCategory", "Pastries");
        
        // Add sample product stock levels
        data.put("product_1", "25"); // Chocolate Cake
        data.put("product_2", "8");  // Vanilla Cupcake
        data.put("product_3", "0");  // Strawberry Tart
        data.put("product_4", "30"); // Blueberry Muffin
        data.put("product_5", "12"); // Cinnamon Roll
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Generate customer behavior analytics.
     *
     * @param startDate the start date for the analytics period
     * @param endDate the end date for the analytics period
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateCustomerBehaviorAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Analytics analytics = new Analytics();
        analytics.setName("Customer Behavior Analysis");
        analytics.setDescription("Customer behavior analysis for the period from " + formatDate(startDate) + " to " + formatDate(endDate));
        analytics.setType(AnalyticsType.CUSTOMER_BEHAVIOR);
        analytics.setTimePeriod("CUSTOM");
        analytics.setStartDate(startDate);
        analytics.setEndDate(endDate);
        
        // In a real implementation, we would calculate the total number of active customers
        // For now, we'll just use a sample value
        BigDecimal activeCustomers = new BigDecimal("250");
        analytics.setNumericValue(activeCustomers);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("totalCustomers", "500");
        data.put("activeCustomers", "250");
        data.put("newCustomers", "75");
        data.put("returningCustomers", "175");
        data.put("averageOrdersPerCustomer", "2.5");
        data.put("topSpendingCustomer", "Alice Brown");
        data.put("mostFrequentCustomer", "John Doe");
        
        // Add customer segments
        data.put("segment_new", "15%");
        data.put("segment_occasional", "35%");
        data.put("segment_regular", "30%");
        data.put("segment_loyal", "20%");
        
        // Add purchase time distribution
        data.put("time_morning", "25%");
        data.put("time_afternoon", "40%");
        data.put("time_evening", "35%");
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Generate product performance analytics.
     *
     * @param startDate the start date for the analytics period
     * @param endDate the end date for the analytics period
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateProductPerformanceAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Analytics analytics = new Analytics();
        analytics.setName("Product Performance Analysis");
        analytics.setDescription("Product performance analysis for the period from " + formatDate(startDate) + " to " + formatDate(endDate));
        analytics.setType(AnalyticsType.PRODUCT_PERFORMANCE);
        analytics.setTimePeriod("CUSTOM");
        analytics.setStartDate(startDate);
        analytics.setEndDate(endDate);
        
        // In a real implementation, we would calculate the total number of products sold
        // For now, we'll just use a sample value
        BigDecimal totalSold = new BigDecimal("340");
        analytics.setNumericValue(totalSold);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("totalProductsSold", "340");
        data.put("totalRevenue", "2500.50");
        data.put("topSellingProduct", "Chocolate Cake");
        data.put("topSellingCategory", "Cakes");
        data.put("lowestSellingProduct", "Rye Bread");
        data.put("highestRatedProduct", "Strawberry Tart");
        
        // Add product sales data
        data.put("product_1_sold", "45");  // Chocolate Cake
        data.put("product_1_revenue", "1125.00");
        data.put("product_1_profit", "450.00");
        data.put("product_1_rating", "4.8");
        
        data.put("product_2_sold", "120"); // Vanilla Cupcake
        data.put("product_2_revenue", "360.00");
        data.put("product_2_profit", "180.00");
        data.put("product_2_rating", "4.5");
        
        data.put("product_3_sold", "30");  // Strawberry Tart
        data.put("product_3_revenue", "450.00");
        data.put("product_3_profit", "225.00");
        data.put("product_3_rating", "4.7");
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Generate financial metrics analytics.
     *
     * @param startDate the start date for the analytics period
     * @param endDate the end date for the analytics period
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateFinancialMetricsAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Analytics analytics = new Analytics();
        analytics.setName("Financial Metrics Analysis");
        analytics.setDescription("Financial metrics analysis for the period from " + formatDate(startDate) + " to " + formatDate(endDate));
        analytics.setType(AnalyticsType.FINANCIAL_METRICS);
        analytics.setTimePeriod("CUSTOM");
        analytics.setStartDate(startDate);
        analytics.setEndDate(endDate);
        
        // In a real implementation, we would calculate the total revenue
        // For now, we'll just use a sample value
        BigDecimal totalRevenue = new BigDecimal("2945.00");
        analytics.setNumericValue(totalRevenue);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("totalRevenue", "2945.00");
        data.put("totalCost", "1178.00");
        data.put("grossProfit", "1767.00");
        data.put("expenses", "750.00");
        data.put("netProfit", "1017.00");
        data.put("profitMargin", "34.5%");
        data.put("averageDailyRevenue", "589.00");
        
        // Add daily financial data
        data.put("day_1_revenue", "525.50");
        data.put("day_1_cost", "210.20");
        data.put("day_1_profit", "315.30");
        
        data.put("day_2_revenue", "478.25");
        data.put("day_2_cost", "191.30");
        data.put("day_2_profit", "286.95");
        
        data.put("day_3_revenue", "645.00");
        data.put("day_3_cost", "258.00");
        data.put("day_3_profit", "387.00");
        
        data.put("day_4_revenue", "700.75");
        data.put("day_4_cost", "280.30");
        data.put("day_4_profit", "420.45");
        
        data.put("day_5_revenue", "595.30");
        data.put("day_5_cost", "238.12");
        data.put("day_5_profit", "357.18");
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Generate dashboard analytics.
     *
     * @return the generated analytics
     */
    @Transactional
    public Analytics generateDashboardAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        
        Analytics analytics = new Analytics();
        analytics.setName("Dashboard Analytics");
        analytics.setDescription("Current dashboard analytics");
        analytics.setType(AnalyticsType.CUSTOM);
        analytics.setTimePeriod("DAILY");
        analytics.setStartDate(startOfDay);
        analytics.setEndDate(now);
        
        // In a real implementation, we would calculate the total revenue for today
        // For now, we'll just use a sample value
        BigDecimal todayRevenue = new BigDecimal("525.50");
        analytics.setNumericValue(todayRevenue);
        
        // Add sample data
        Map<String, String> data = new HashMap<>();
        data.put("todayOrders", "15");
        data.put("todayRevenue", "525.50");
        data.put("todayCustomers", "12");
        data.put("pendingOrders", "3");
        data.put("lowStockItems", "5");
        data.put("outOfStockItems", "2");
        
        // Add comparison with yesterday
        data.put("ordersChangePercent", "+5.2%");
        data.put("revenueChangePercent", "+3.8%");
        data.put("customersChangePercent", "+8.1%");
        
        // Add top selling products for today
        data.put("topProduct1", "Chocolate Cake");
        data.put("topProduct1Sold", "5");
        data.put("topProduct2", "Vanilla Cupcake");
        data.put("topProduct2Sold", "12");
        data.put("topProduct3", "Blueberry Muffin");
        data.put("topProduct3Sold", "8");
        
        analytics.setData(data);
        
        return analyticsRepository.save(analytics);
    }
    
    /**
     * Delete analytics.
     *
     * @param id the analytics ID
     */
    @Transactional
    public void deleteAnalytics(Long id) {
        Analytics analytics = getAnalyticsById(id);
        analyticsRepository.deleteById(id);
    }
    
    // Helper methods
    
    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}