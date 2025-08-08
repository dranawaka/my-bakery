package com.aurelius.tech.mybakery.dto;

import com.aurelius.tech.mybakery.model.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for CartItem that flattens the structure for frontend consumption.
 */
public class CartItemDTO {
    
    private Long id;
    private Long productId;
    private String name;
    private String sku;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
    private Boolean savedForLater;
    
    /**
     * Default constructor
     */
    public CartItemDTO() {}
    
    /**
     * Constructor from CartItem entity
     */
    public CartItemDTO(CartItem cartItem) {
        this.id = cartItem.getId();
        
        // Safely handle product data
        if (cartItem.getProduct() != null) {
            this.productId = cartItem.getProduct().getId();
            this.name = cartItem.getProduct().getName();
            this.sku = cartItem.getProduct().getSku();
            this.imageUrl = cartItem.getProduct().getImageUrl();
        } else {
            this.productId = null;
            this.name = "Product not available";
            this.sku = "N/A";
            this.imageUrl = null;
        }
        
        this.price = cartItem.getUnitPrice();
        this.quantity = cartItem.getQuantity();
        this.totalPrice = cartItem.getTotalPrice();
        this.addedAt = cartItem.getAddedAt();
        this.updatedAt = cartItem.getUpdatedAt();
        this.savedForLater = cartItem.getSavedForLater();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public LocalDateTime getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getSavedForLater() {
        return savedForLater;
    }
    
    public void setSavedForLater(Boolean savedForLater) {
        this.savedForLater = savedForLater;
    }
} 