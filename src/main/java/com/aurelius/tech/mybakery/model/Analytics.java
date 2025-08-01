package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing analytics data in the system.
 */
@Entity
@Table(name = "analytics")
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalyticsType type;
    
    @Column(name = "time_period")
    private String timePeriod;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "numeric_value")
    private BigDecimal numericValue;
    
    @ElementCollection
    @CollectionTable(name = "analytics_data", joinColumns = @JoinColumn(name = "analytics_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> data = new HashMap<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing the type of analytics.
     */
    public enum AnalyticsType {
        SALES_TREND,
        INVENTORY_LEVEL,
        CUSTOMER_BEHAVIOR,
        PRODUCT_PERFORMANCE,
        FINANCIAL_METRICS,
        CUSTOM
    }
    
    /**
     * Pre-persist hook to set created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Pre-update hook to update the updated timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    
    public Analytics() {
    }
    
    public Analytics(String name, String description, AnalyticsType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public AnalyticsType getType() {
        return type;
    }
    
    public void setType(AnalyticsType type) {
        this.type = type;
    }
    
    public String getTimePeriod() {
        return timePeriod;
    }
    
    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public BigDecimal getNumericValue() {
        return numericValue;
    }
    
    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }
    
    public Map<String, String> getData() {
        return data;
    }
    
    public void setData(Map<String, String> data) {
        this.data = data;
    }
    
    public void addData(String key, String value) {
        this.data.put(key, value);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Analytics{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", timePeriod='" + timePeriod + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}