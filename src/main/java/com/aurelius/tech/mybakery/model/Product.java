package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Product entity representing bakery products in the system.
 */
@Entity
@Table(name = "products", uniqueConstraints = {
    @UniqueConstraint(columnNames = "sku"),
    @UniqueConstraint(columnNames = "barcode")
})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "short_description")
    private String shortDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    
    @Column(name = "original_price")
    private BigDecimal originalPrice;
    
    private Integer discount;
    
    @Column(unique = true)
    private String sku;
    
    @Column(unique = true)
    private String barcode;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_nutritional_info", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "nutrient_name")
    @Column(name = "nutrient_value")
    private Map<String, String> nutritionalInfo = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "product_allergens", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "allergen")
    private Set<String> allergens = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "product_ingredients", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "ingredient")
    private Set<String> ingredients = new HashSet<>();
    
    @Column(name = "preparation_time")
    private Integer preparationTime;
    
    @Column(name = "shelf_life")
    private String shelfLife;
    
    @Column(name = "storage_requirements")
    private String storageRequirements;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
    
    private boolean active = true;
    
    private boolean featured = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "rating_count")
    private Integer ratingCount;
    
    /**
     * Default constructor
     */
    public Product() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Product(Long id, String name, String description, Category category, 
                  BigDecimal price, BigDecimal costPrice, String sku, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.costPrice = costPrice;
        this.sku = sku;
        this.active = active;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Integer getDiscount() {
        return discount;
    }
    
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public Map<String, String> getNutritionalInfo() {
        return nutritionalInfo;
    }
    
    public void setNutritionalInfo(Map<String, String> nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }
    
    public Set<String> getAllergens() {
        return allergens;
    }
    
    public void setAllergens(Set<String> allergens) {
        this.allergens = allergens;
    }
    
    public Set<String> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(Set<String> ingredients) {
        this.ingredients = ingredients;
    }
    
    public Integer getPreparationTime() {
        return preparationTime;
    }
    
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }
    
    public String getShelfLife() {
        return shelfLife;
    }
    
    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }
    
    public String getStorageRequirements() {
        return storageRequirements;
    }
    
    public void setStorageRequirements(String storageRequirements) {
        this.storageRequirements = storageRequirements;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
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
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", sku='" + sku + '\'' +
                ", active=" + active +
                ", category=" + (category != null ? category.getId() : "null") +
                ", stockQuantity=" + stockQuantity +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}