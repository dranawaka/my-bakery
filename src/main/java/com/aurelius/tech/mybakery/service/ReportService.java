package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Report;
import com.aurelius.tech.mybakery.model.Report.ReportType;
import com.aurelius.tech.mybakery.repository.ReportRepository;
import com.aurelius.tech.mybakery.repository.OrderRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;
import com.aurelius.tech.mybakery.repository.InventoryRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for handling report operations.
 */
@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    
    // Base directory for storing report files
    private static final String REPORTS_DIR = "./reports";
    
    /**
     * Constructor with dependencies.
     */
    public ReportService(
            ReportRepository reportRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        
        // Create reports directory if it doesn't exist
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }
    
    /**
     * Get a report by ID.
     *
     * @param id the report ID
     * @return the report with the specified ID
     * @throws RuntimeException if the report is not found
     */
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }
    
    /**
     * Get all reports.
     *
     * @return a list of all reports
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    /**
     * Get reports by type.
     *
     * @param type the report type
     * @return a list of reports with the specified type
     */
    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }
    
    /**
     * Get reports by creator.
     *
     * @param userId the user ID of the creator
     * @return a list of reports created by the specified user
     */
    public List<Report> getReportsByCreator(Long userId) {
        return reportRepository.findByCreatedBy(userId);
    }
    
    /**
     * Get reports created between the specified dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of reports created between the specified dates
     */
    public List<Report> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Generate a sales report.
     *
     * @param startDate the start date for the report period
     * @param endDate the end date for the report period
     * @param userId the ID of the user generating the report
     * @param fileType the file type for the report (CSV, PDF, etc.)
     * @return the generated report
     */
    @Transactional
    public Report generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, Long userId, String fileType) {
        Report report = new Report();
        report.setName("Sales Report - " + formatDateRange(startDate, endDate));
        report.setDescription("Sales report for the period from " + startDate + " to " + endDate);
        report.setType(ReportType.SALES);
        report.setCreatedBy(userId);
        
        // Add report parameters
        report.addParameter("startDate", startDate.toString());
        report.addParameter("endDate", endDate.toString());
        
        // Generate the report file
        String fileName = generateFileName("sales", fileType);
        String filePath = REPORTS_DIR + "/" + fileName;
        
        try {
            // In a real implementation, we would generate a proper report file
            // For now, we'll just create a simple CSV file with some sample data
            generateSampleSalesReportFile(filePath, startDate, endDate);
            
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setFileSize(new File(filePath).length());
            
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate sales report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate an inventory report.
     *
     * @param userId the ID of the user generating the report
     * @param fileType the file type for the report (CSV, PDF, etc.)
     * @return the generated report
     */
    @Transactional
    public Report generateInventoryReport(Long userId, String fileType) {
        Report report = new Report();
        report.setName("Inventory Report - " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        report.setDescription("Current inventory status report");
        report.setType(ReportType.INVENTORY);
        report.setCreatedBy(userId);
        
        // Generate the report file
        String fileName = generateFileName("inventory", fileType);
        String filePath = REPORTS_DIR + "/" + fileName;
        
        try {
            // In a real implementation, we would generate a proper report file
            // For now, we'll just create a simple CSV file with some sample data
            generateSampleInventoryReportFile(filePath);
            
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setFileSize(new File(filePath).length());
            
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate inventory report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate a customer report.
     *
     * @param startDate the start date for the report period
     * @param endDate the end date for the report period
     * @param userId the ID of the user generating the report
     * @param fileType the file type for the report (CSV, PDF, etc.)
     * @return the generated report
     */
    @Transactional
    public Report generateCustomerReport(LocalDateTime startDate, LocalDateTime endDate, Long userId, String fileType) {
        Report report = new Report();
        report.setName("Customer Report - " + formatDateRange(startDate, endDate));
        report.setDescription("Customer activity report for the period from " + startDate + " to " + endDate);
        report.setType(ReportType.CUSTOMER);
        report.setCreatedBy(userId);
        
        // Add report parameters
        report.addParameter("startDate", startDate.toString());
        report.addParameter("endDate", endDate.toString());
        
        // Generate the report file
        String fileName = generateFileName("customer", fileType);
        String filePath = REPORTS_DIR + "/" + fileName;
        
        try {
            // In a real implementation, we would generate a proper report file
            // For now, we'll just create a simple CSV file with some sample data
            generateSampleCustomerReportFile(filePath, startDate, endDate);
            
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setFileSize(new File(filePath).length());
            
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate customer report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate a product report.
     *
     * @param startDate the start date for the report period
     * @param endDate the end date for the report period
     * @param userId the ID of the user generating the report
     * @param fileType the file type for the report (CSV, PDF, etc.)
     * @return the generated report
     */
    @Transactional
    public Report generateProductReport(LocalDateTime startDate, LocalDateTime endDate, Long userId, String fileType) {
        Report report = new Report();
        report.setName("Product Report - " + formatDateRange(startDate, endDate));
        report.setDescription("Product performance report for the period from " + startDate + " to " + endDate);
        report.setType(ReportType.PRODUCT);
        report.setCreatedBy(userId);
        
        // Add report parameters
        report.addParameter("startDate", startDate.toString());
        report.addParameter("endDate", endDate.toString());
        
        // Generate the report file
        String fileName = generateFileName("product", fileType);
        String filePath = REPORTS_DIR + "/" + fileName;
        
        try {
            // In a real implementation, we would generate a proper report file
            // For now, we'll just create a simple CSV file with some sample data
            generateSampleProductReportFile(filePath, startDate, endDate);
            
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setFileSize(new File(filePath).length());
            
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate product report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate a financial report.
     *
     * @param startDate the start date for the report period
     * @param endDate the end date for the report period
     * @param userId the ID of the user generating the report
     * @param fileType the file type for the report (CSV, PDF, etc.)
     * @return the generated report
     */
    @Transactional
    public Report generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate, Long userId, String fileType) {
        Report report = new Report();
        report.setName("Financial Report - " + formatDateRange(startDate, endDate));
        report.setDescription("Financial report for the period from " + startDate + " to " + endDate);
        report.setType(ReportType.FINANCIAL);
        report.setCreatedBy(userId);
        
        // Add report parameters
        report.addParameter("startDate", startDate.toString());
        report.addParameter("endDate", endDate.toString());
        
        // Generate the report file
        String fileName = generateFileName("financial", fileType);
        String filePath = REPORTS_DIR + "/" + fileName;
        
        try {
            // In a real implementation, we would generate a proper report file
            // For now, we'll just create a simple CSV file with some sample data
            generateSampleFinancialReportFile(filePath, startDate, endDate);
            
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setFileSize(new File(filePath).length());
            
            return reportRepository.save(report);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate financial report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a report.
     *
     * @param id the report ID
     */
    @Transactional
    public void deleteReport(Long id) {
        Report report = getReportById(id);
        
        // Delete the report file if it exists
        if (report.getFilePath() != null) {
            File file = new File(report.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        
        reportRepository.deleteById(id);
    }
    
    // Helper methods
    
    private String generateFileName(String prefix, String fileType) {
        return prefix + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
               "_" + UUID.randomUUID().toString().substring(0, 8) + "." + fileType.toLowerCase();
    }
    
    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return startDate.format(formatter) + " to " + endDate.format(formatter);
    }
    
    // Sample report file generators
    
    private void generateSampleSalesReportFile(String filePath, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Date,Order ID,Customer,Total Amount,Items,Status\n");
            writer.write("2023-01-01,ORD-12345,John Doe,$125.50,5,COMPLETED\n");
            writer.write("2023-01-02,ORD-12346,Jane Smith,$78.25,3,COMPLETED\n");
            writer.write("2023-01-03,ORD-12347,Bob Johnson,$45.00,2,COMPLETED\n");
            writer.write("2023-01-04,ORD-12348,Alice Brown,$200.75,8,COMPLETED\n");
            writer.write("2023-01-05,ORD-12349,Charlie Davis,$95.30,4,COMPLETED\n");
        }
    }
    
    private void generateSampleInventoryReportFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Product ID,Product Name,Current Stock,Reorder Point,Status\n");
            writer.write("1,Chocolate Cake,25,10,IN_STOCK\n");
            writer.write("2,Vanilla Cupcake,8,15,LOW_STOCK\n");
            writer.write("3,Strawberry Tart,0,5,OUT_OF_STOCK\n");
            writer.write("4,Blueberry Muffin,30,10,IN_STOCK\n");
            writer.write("5,Cinnamon Roll,12,10,IN_STOCK\n");
        }
    }
    
    private void generateSampleCustomerReportFile(String filePath, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Customer ID,Name,Email,Orders,Total Spent,Avg Order Value,Loyalty Points\n");
            writer.write("1,John Doe,john@example.com,5,$250.75,$50.15,125\n");
            writer.write("2,Jane Smith,jane@example.com,3,$178.50,$59.50,89\n");
            writer.write("3,Bob Johnson,bob@example.com,2,$95.25,$47.63,47\n");
            writer.write("4,Alice Brown,alice@example.com,8,$520.00,$65.00,260\n");
            writer.write("5,Charlie Davis,charlie@example.com,4,$210.80,$52.70,105\n");
        }
    }
    
    private void generateSampleProductReportFile(String filePath, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Product ID,Product Name,Units Sold,Revenue,Profit,Avg Rating\n");
            writer.write("1,Chocolate Cake,45,$1125.00,$450.00,4.8\n");
            writer.write("2,Vanilla Cupcake,120,$360.00,$180.00,4.5\n");
            writer.write("3,Strawberry Tart,30,$450.00,$225.00,4.7\n");
            writer.write("4,Blueberry Muffin,80,$240.00,$120.00,4.2\n");
            writer.write("5,Cinnamon Roll,65,$325.00,$162.50,4.6\n");
        }
    }
    
    private void generateSampleFinancialReportFile(String filePath, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Date,Revenue,Cost of Goods,Gross Profit,Expenses,Net Profit\n");
            writer.write("2023-01-01,$525.50,$210.20,$315.30,$150.00,$165.30\n");
            writer.write("2023-01-02,$478.25,$191.30,$286.95,$150.00,$136.95\n");
            writer.write("2023-01-03,$645.00,$258.00,$387.00,$150.00,$237.00\n");
            writer.write("2023-01-04,$700.75,$280.30,$420.45,$150.00,$270.45\n");
            writer.write("2023-01-05,$595.30,$238.12,$357.18,$150.00,$207.18\n");
        }
    }
}