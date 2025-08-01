package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a recipe for a product.
 */
@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(name = "product_id")
    private Long productId;
    
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @Column(name = "preparation_time")
    private Integer preparationTime;
    
    @Column(name = "cooking_time")
    private Integer cookingTime;
    
    @Column(name = "resting_time")
    private Integer restingTime;
    
    @Column(name = "total_time")
    private Integer totalTime;
    
    @Column(name = "serving_size")
    private Integer servingSize;
    
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    
    /**
     * Enum representing the difficulty level of a recipe.
     */
    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD,
        EXPERT
    }
    
    /**
     * Pre-persist hook to set created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
        
        // Calculate total time if not set
        if (this.totalTime == null && this.preparationTime != null) {
            this.totalTime = this.preparationTime;
            if (this.cookingTime != null) {
                this.totalTime += this.cookingTime;
            }
            if (this.restingTime != null) {
                this.totalTime += this.restingTime;
            }
        }
    }
    
    /**
     * Pre-update hook to update the updated timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // Recalculate total time
        if (this.preparationTime != null) {
            this.totalTime = this.preparationTime;
            if (this.cookingTime != null) {
                this.totalTime += this.cookingTime;
            }
            if (this.restingTime != null) {
                this.totalTime += this.restingTime;
            }
        }
    }
    
    // Constructors
    public Recipe() {
    }
    
    public Recipe(String name, String description, Long productId, Integer preparationTime,
                 Integer cookingTime, Integer restingTime, Integer servingSize,
                 DifficultyLevel difficultyLevel, String instructions) {
        this.name = name;
        this.description = description;
        this.productId = productId;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.restingTime = restingTime;
        this.servingSize = servingSize;
        this.difficultyLevel = difficultyLevel;
        this.instructions = instructions;
        this.isActive = true;
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
    
    public Integer getPreparationTime() {
        return preparationTime;
    }
    
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }
    
    public Integer getCookingTime() {
        return cookingTime;
    }
    
    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }
    
    public Integer getRestingTime() {
        return restingTime;
    }
    
    public void setRestingTime(Integer restingTime) {
        this.restingTime = restingTime;
    }
    
    public Integer getTotalTime() {
        return totalTime;
    }
    
    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }
    
    public Integer getServingSize() {
        return servingSize;
    }
    
    public void setServingSize(Integer servingSize) {
        this.servingSize = servingSize;
    }
    
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean active) {
        isActive = active;
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
    
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
    
    /**
     * Add an ingredient to the recipe.
     *
     * @param ingredient the ingredient to add
     */
    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }
    
    /**
     * Remove an ingredient from the recipe.
     *
     * @param ingredient the ingredient to remove
     */
    public void removeIngredient(RecipeIngredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id) &&
               Objects.equals(name, recipe.name) &&
               Objects.equals(productId, recipe.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, productId);
    }
    
    @Override
    public String toString() {
        return "Recipe{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", productId=" + productId +
               ", preparationTime=" + preparationTime +
               ", cookingTime=" + cookingTime +
               ", restingTime=" + restingTime +
               ", totalTime=" + totalTime +
               ", servingSize=" + servingSize +
               ", difficultyLevel=" + difficultyLevel +
               ", isActive=" + isActive +
               '}';
    }
}