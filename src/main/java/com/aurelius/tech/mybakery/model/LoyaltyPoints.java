package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing customer loyalty points.
 */
@Entity
@Table(name = "loyalty_points")
public class LoyaltyPoints {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private User customer;
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "transaction_reference")
    private String transactionReference;
    
    @Column
    private String description;
    
    /**
     * Enum representing the type of loyalty point transaction.
     */
    public enum TransactionType {
        EARN,
        REDEEM,
        EXPIRE,
        ADJUST
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
    public LoyaltyPoints() {
    }
    
    public LoyaltyPoints(Long customerId, Integer points, Integer totalPoints, 
                        TransactionType transactionType, String transactionReference, 
                        String description, LocalDateTime expiryDate) {
        this.customerId = customerId;
        this.points = points;
        this.totalPoints = totalPoints;
        this.transactionType = transactionType;
        this.transactionReference = transactionReference;
        this.description = description;
        this.expiryDate = expiryDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
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
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public String getTransactionReference() {
        return transactionReference;
    }
    
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoyaltyPoints that = (LoyaltyPoints) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(points, that.points) &&
               Objects.equals(totalPoints, that.totalPoints) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt) &&
               Objects.equals(expiryDate, that.expiryDate) &&
               transactionType == that.transactionType &&
               Objects.equals(transactionReference, that.transactionReference) &&
               Objects.equals(description, that.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, points, totalPoints, createdAt, updatedAt, 
                           expiryDate, transactionType, transactionReference, description);
    }
    
    @Override
    public String toString() {
        return "LoyaltyPoints{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", points=" + points +
               ", totalPoints=" + totalPoints +
               ", transactionType=" + transactionType +
               ", transactionReference='" + transactionReference + '\'' +
               ", description='" + description + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", expiryDate=" + expiryDate +
               '}';
    }
}