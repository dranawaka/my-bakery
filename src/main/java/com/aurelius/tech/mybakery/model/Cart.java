package com.aurelius.tech.mybakery.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart entity representing a shopping cart in the system.
 */
@Entity
@Table(name = "carts")
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    /**
     * Default constructor
     */
    public Cart() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Set expiration to 30 days from now by default
        this.expiresAt = LocalDateTime.now().plusDays(30);
        this.totalAmount = BigDecimal.ZERO;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
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
    
    /**
     * Add an item to the cart.
     *
     * @param item the item to add
     * @return the updated cart
     */
    public Cart addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        recalculateTotalAmount();
        return this;
    }
    
    /**
     * Remove an item from the cart.
     *
     * @param item the item to remove
     * @return the updated cart
     */
    public Cart removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        recalculateTotalAmount();
        return this;
    }
    
    /**
     * Recalculate the total amount of the cart.
     */
    public void recalculateTotalAmount() {
        this.totalAmount = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal itemTotal = item.getTotalPrice();
            if (itemTotal != null) {
                this.totalAmount = this.totalAmount.add(itemTotal);
            }
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if the cart is expired.
     *
     * @return true if the cart is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Clear all items from the cart.
     */
    public void clear() {
        items.clear();
        this.totalAmount = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }
}