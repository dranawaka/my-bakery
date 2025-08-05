package com.aurelius.tech.mybakery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * OrderItem entity representing items in an order.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull
    @Column(name = "unit_price", nullable = false)
    @JsonProperty(value = "price", access = JsonProperty.Access.READ_WRITE)
    private BigDecimal unitPrice;
    
    @NotNull
    @Column(name = "total_price", nullable = false)
    @JsonProperty(value = "subtotal", access = JsonProperty.Access.READ_WRITE)
    private BigDecimal totalPrice;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Transient
    private Long productId;
    
    @Transient
    private Long orderId;
    
    /**
     * Default constructor
     */
    public OrderItem() {
    }
    
    /**
     * Constructor with essential fields
     */
    public OrderItem(Long id, Order order, Product product, Integer quantity, 
                    BigDecimal unitPrice, BigDecimal totalPrice) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Long getOrderId() {
        if (order != null) {
            return order.getId();
        }
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        // This method is used by services to set the order ID before loading the actual order
        // The actual Order object should be set using setOrder() when available
        this.orderId = orderId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Long getProductId() {
        if (product != null) {
            return product.getId();
        }
        return productId;
    }
    
    public void setProductId(Long productId) {
        // This method is used by services to set the product ID before loading the actual product
        // The actual Product object should be set using setProduct() when available
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    /**
     * Calculate the total price based on quantity and unit price.
     */
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}