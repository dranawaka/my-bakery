package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a customer's usage of a promotion.
 */
@Entity
@Table(name = "promotion_usages")
public class PromotionUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id", insertable = false, updatable = false)
    private Promotion promotion;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private User customer;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
    
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
    
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
    
    /**
     * Pre-persist hook to set the used timestamp.
     */
    @PrePersist
    protected void onCreate() {
        this.usedAt = LocalDateTime.now();
    }
    
    // Constructors
    public PromotionUsage() {
    }
    
    public PromotionUsage(Long promotionId, Long customerId, Long orderId, BigDecimal discountAmount) {
        this.promotionId = promotionId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPromotionId() {
        return promotionId;
    }
    
    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }
    
    public Promotion getPromotion() {
        return promotion;
    }
    
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
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
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionUsage that = (PromotionUsage) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(promotionId, that.promotionId) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(usedAt, that.usedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, promotionId, customerId, orderId, usedAt);
    }
    
    @Override
    public String toString() {
        return "PromotionUsage{" +
               "id=" + id +
               ", promotionId=" + promotionId +
               ", customerId=" + customerId +
               ", orderId=" + orderId +
               ", discountAmount=" + discountAmount +
               ", usedAt=" + usedAt +
               '}';
    }
}