package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing loyalty tiers with their respective benefits.
 */
@Entity
@Table(name = "loyalty_tiers")
public class LoyaltyTier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(name = "points_threshold", nullable = false)
    private Integer pointsThreshold;
    
    @Column(name = "points_multiplier")
    private Double pointsMultiplier;
    
    @Column(name = "discount_percentage")
    private Integer discountPercentage;
    
    @Column(name = "free_shipping")
    private Boolean freeShipping;
    
    @Column(name = "birthday_bonus")
    private Boolean birthdayBonus;
    
    @Column(name = "exclusive_offers")
    private Boolean exclusiveOffers;
    
    @Column(name = "priority_support")
    private Boolean prioritySupport;
    
    @Column(name = "early_access")
    private Boolean earlyAccess;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
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
        if (this.pointsMultiplier == null) {
            this.pointsMultiplier = 1.0;
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
    public LoyaltyTier() {
    }
    
    public LoyaltyTier(String name, String description, Integer pointsThreshold, 
                      Double pointsMultiplier, Boolean active) {
        this.name = name;
        this.description = description;
        this.pointsThreshold = pointsThreshold;
        this.pointsMultiplier = pointsMultiplier;
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
    
    public Integer getPointsThreshold() {
        return pointsThreshold;
    }
    
    public void setPointsThreshold(Integer pointsThreshold) {
        this.pointsThreshold = pointsThreshold;
    }
    
    public Double getPointsMultiplier() {
        return pointsMultiplier;
    }
    
    public void setPointsMultiplier(Double pointsMultiplier) {
        this.pointsMultiplier = pointsMultiplier;
    }
    
    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public Boolean getFreeShipping() {
        return freeShipping;
    }
    
    public void setFreeShipping(Boolean freeShipping) {
        this.freeShipping = freeShipping;
    }
    
    public Boolean getBirthdayBonus() {
        return birthdayBonus;
    }
    
    public void setBirthdayBonus(Boolean birthdayBonus) {
        this.birthdayBonus = birthdayBonus;
    }
    
    public Boolean getExclusiveOffers() {
        return exclusiveOffers;
    }
    
    public void setExclusiveOffers(Boolean exclusiveOffers) {
        this.exclusiveOffers = exclusiveOffers;
    }
    
    public Boolean getPrioritySupport() {
        return prioritySupport;
    }
    
    public void setPrioritySupport(Boolean prioritySupport) {
        this.prioritySupport = prioritySupport;
    }
    
    public Boolean getEarlyAccess() {
        return earlyAccess;
    }
    
    public void setEarlyAccess(Boolean earlyAccess) {
        this.earlyAccess = earlyAccess;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
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
        LoyaltyTier that = (LoyaltyTier) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(pointsThreshold, that.pointsThreshold) &&
               Objects.equals(pointsMultiplier, that.pointsMultiplier) &&
               Objects.equals(discountPercentage, that.discountPercentage) &&
               Objects.equals(freeShipping, that.freeShipping) &&
               Objects.equals(birthdayBonus, that.birthdayBonus) &&
               Objects.equals(exclusiveOffers, that.exclusiveOffers) &&
               Objects.equals(prioritySupport, that.prioritySupport) &&
               Objects.equals(earlyAccess, that.earlyAccess) &&
               Objects.equals(active, that.active);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, pointsThreshold, pointsMultiplier, discountPercentage,
                           freeShipping, birthdayBonus, exclusiveOffers, prioritySupport, 
                           earlyAccess, active);
    }
    
    @Override
    public String toString() {
        return "LoyaltyTier{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", pointsThreshold=" + pointsThreshold +
               ", pointsMultiplier=" + pointsMultiplier +
               ", discountPercentage=" + discountPercentage +
               ", freeShipping=" + freeShipping +
               ", birthdayBonus=" + birthdayBonus +
               ", exclusiveOffers=" + exclusiveOffers +
               ", prioritySupport=" + prioritySupport +
               ", earlyAccess=" + earlyAccess +
               ", active=" + active +
               '}';
    }
}