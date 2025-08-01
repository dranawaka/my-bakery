package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a promotion or discount.
 */
@Entity
@Table(name = "promotions")
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(name = "promo_code", unique = true)
    private String promoCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;
    
    @Column(name = "minimum_order_value")
    private BigDecimal minimumOrderValue;
    
    @Column(name = "maximum_discount")
    private BigDecimal maximumDiscount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    @Column(name = "product_id")
    private Long productId;
    
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing the type of discount.
     */
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        BUY_ONE_GET_ONE,
        FREE_SHIPPING
    }
    
    /**
     * Pre-persist hook to set created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    /**
     * Pre-update hook to update the updated timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if the promotion is valid for the current date.
     *
     * @return true if the promotion is valid, false otherwise
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               now.isAfter(startDate) && 
               (endDate == null || now.isBefore(endDate)) &&
               (usageLimit == null || usageCount < usageLimit);
    }
    
    /**
     * Calculate the discount amount for an order.
     *
     * @param orderTotal the order total
     * @return the discount amount
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        // Check if minimum order value is met
        if (minimumOrderValue != null && orderTotal.compareTo(minimumOrderValue) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        
        switch (discountType) {
            case PERCENTAGE:
                // Calculate percentage discount
                discount = orderTotal.multiply(discountValue.divide(new BigDecimal("100")));
                break;
            case FIXED_AMOUNT:
                // Fixed amount discount
                discount = discountValue;
                break;
            case BUY_ONE_GET_ONE:
                // For simplicity, assume 50% off
                discount = orderTotal.multiply(new BigDecimal("0.5"));
                break;
            case FREE_SHIPPING:
                // Assume a fixed shipping cost for simplicity
                discount = new BigDecimal("5.00");
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        
        // Apply maximum discount if specified
        if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
            discount = maximumDiscount;
        }
        
        return discount;
    }
    
    // Constructors
    public Promotion() {
    }
    
    public Promotion(String name, String description, String promoCode, DiscountType discountType,
                    BigDecimal discountValue, LocalDateTime startDate, LocalDateTime endDate, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.promoCode = promoCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.usageCount = 0;
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
    
    public String getPromoCode() {
        return promoCode;
    }
    
    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    
    public BigDecimal getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }
    
    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
    }
    
    public void setMinimumOrderValue(BigDecimal minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }
    
    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }
    
    public void setMaximumDiscount(BigDecimal maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }
    
    public Integer getUsageLimit() {
        return usageLimit;
    }
    
    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean active) {
        isActive = active;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
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
        Promotion promotion = (Promotion) o;
        return Objects.equals(id, promotion.id) &&
               Objects.equals(name, promotion.name) &&
               Objects.equals(promoCode, promotion.promoCode) &&
               discountType == promotion.discountType &&
               Objects.equals(discountValue, promotion.discountValue) &&
               Objects.equals(startDate, promotion.startDate) &&
               Objects.equals(endDate, promotion.endDate) &&
               Objects.equals(isActive, promotion.isActive);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, promoCode, discountType, discountValue, startDate, endDate, isActive);
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", promoCode='" + promoCode + '\'' +
               ", discountType=" + discountType +
               ", discountValue=" + discountValue +
               ", minimumOrderValue=" + minimumOrderValue +
               ", maximumDiscount=" + maximumDiscount +
               ", usageLimit=" + usageLimit +
               ", usageCount=" + usageCount +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", isActive=" + isActive +
               '}';
    }
}