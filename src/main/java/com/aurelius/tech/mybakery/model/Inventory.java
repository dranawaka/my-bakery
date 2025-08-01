package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Inventory entity representing product inventory in the system.
 */
@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "reorder_point")
    private Integer reorderPoint;
    
    @Column(name = "reorder_quantity")
    private Integer reorderQuantity;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;
    
    /**
     * Default constructor
     */
    public Inventory() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Inventory(Long id, Product product, Integer quantity, Integer reorderPoint, 
                    Integer reorderQuantity, LocalDateTime lastUpdated) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.lastUpdated = lastUpdated;
        this.status = determineStatus();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }
    
    public void setProductId(Long productId) {
        // This method is used by services to set the product ID before loading the actual product
        // The actual Product object should be set using setProduct() when available
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
        this.status = determineStatus();
    }
    
    public Integer getReorderPoint() {
        return reorderPoint;
    }
    
    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }
    
    public Integer getReorderQuantity() {
        return reorderQuantity;
    }
    
    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getBatchNumber() {
        return batchNumber;
    }
    
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public InventoryStatus getStatus() {
        return status;
    }
    
    public void setStatus(InventoryStatus status) {
        this.status = status;
    }
    
    /**
     * Determine inventory status based on quantity and reorder point.
     */
    public InventoryStatus determineStatus() {
        if (quantity == null || reorderPoint == null) {
            return InventoryStatus.UNKNOWN;
        }
        
        if (quantity <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (quantity < reorderPoint) {
            return InventoryStatus.LOW_STOCK;
        } else {
            return InventoryStatus.IN_STOCK;
        }
    }
    
    /**
     * Check if the product needs to be reordered.
     */
    public boolean needsReorder() {
        return quantity != null && reorderPoint != null && quantity <= reorderPoint;
    }
    
    /**
     * Enum representing inventory status in the system.
     */
    public enum InventoryStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK,
        EXPIRED,
        UNKNOWN
    }
}