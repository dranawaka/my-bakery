package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing loyalty rewards that customers can redeem.
 */
@Entity
@Table(name = "loyalty_rewards")
public class LoyaltyReward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private Integer pointsCost;
    
    @Column
    private BigDecimal discountAmount;
    
    @Column
    private Integer discountPercentage;
    
    @Column(name = "product_id")
    private Long productId;
    
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing the type of loyalty reward.
     */
    public enum RewardType {
        DISCOUNT_AMOUNT,
        DISCOUNT_PERCENTAGE,
        FREE_PRODUCT,
        FREE_SHIPPING,
        SPECIAL_OFFER
    }
    
    /**
     * Pre-persist hook to set created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }
    
    /**
     * Pre-update hook to update the updated timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public LoyaltyReward() {
    }
    
    public LoyaltyReward(String name, String description, Integer pointsCost, 
                        RewardType rewardType, Boolean active) {
        this.name = name;
        this.description = description;
        this.pointsCost = pointsCost;
        this.rewardType = rewardType;
        this.active = active;
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
    
    public Integer getPointsCost() {
        return pointsCost;
    }
    
    public void setPointsCost(Integer pointsCost) {
        this.pointsCost = pointsCost;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public RewardType getRewardType() {
        return rewardType;
    }
    
    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
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
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoyaltyReward that = (LoyaltyReward) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(pointsCost, that.pointsCost) &&
               Objects.equals(discountAmount, that.discountAmount) &&
               Objects.equals(discountPercentage, that.discountPercentage) &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(active, that.active) &&
               rewardType == that.rewardType &&
               Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, pointsCost, discountAmount, discountPercentage, 
                           productId, active, rewardType, startDate, endDate);
    }
    
    @Override
    public String toString() {
        return "LoyaltyReward{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", pointsCost=" + pointsCost +
               ", rewardType=" + rewardType +
               ", active=" + active +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               '}';
    }
}