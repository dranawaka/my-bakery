package com.aurelius.tech.mybakery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity representing an ingredient in a recipe.
 */
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    @JsonBackReference
    private Recipe recipe;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal quantity;
    
    @Column(nullable = false)
    private String unit;
    
    @Column
    private String notes;
    
    @Column(name = "is_optional")
    private Boolean isOptional;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    // Constructors
    public RecipeIngredient() {
    }
    
    public RecipeIngredient(Long recipeId, String name, BigDecimal quantity, String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.isOptional = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsOptional() {
        return isOptional;
    }
    
    public void setIsOptional(Boolean optional) {
        isOptional = optional;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(recipeId, that.recipeId) &&
               Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, recipeId, name);
    }
    
    @Override
    public String toString() {
        return "RecipeIngredient{" +
               "id=" + id +
               ", recipeId=" + recipeId +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", unit='" + unit + '\'' +
               ", isOptional=" + isOptional +
               '}';
    }
}