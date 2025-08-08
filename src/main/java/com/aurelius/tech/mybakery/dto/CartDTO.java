package com.aurelius.tech.mybakery.dto;

import com.aurelius.tech.mybakery.model.Cart;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for Cart that provides a flattened structure for frontend consumption.
 */
public class CartDTO {
    
    private Long id;
    private String sessionId;
    private Long userId;
    private List<CartItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private BigDecimal totalAmount;
    
    /**
     * Default constructor
     */
    public CartDTO() {}
    
    /**
     * Constructor from Cart entity
     */
    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.sessionId = cart.getSessionId();
        this.userId = cart.getUser() != null ? cart.getUser().getId() : null;
        this.items = cart.getItems().stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();
        this.expiresAt = cart.getExpiresAt();
        this.totalAmount = cart.getTotalAmount();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<CartItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
} 