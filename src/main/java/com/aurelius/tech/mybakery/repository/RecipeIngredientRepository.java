package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing recipe ingredients.
 */
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    
    /**
     * Find all ingredients for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return a list of ingredients for the specified recipe
     */
    List<RecipeIngredient> findByRecipeId(Long recipeId);
    
    /**
     * Find all ingredients for a specific recipe, ordered by sort order.
     *
     * @param recipeId the recipe ID
     * @return a list of ingredients for the specified recipe, ordered by sort order
     */
    List<RecipeIngredient> findByRecipeIdOrderBySortOrderAsc(Long recipeId);
    
    /**
     * Find all optional ingredients for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return a list of optional ingredients for the specified recipe
     */
    List<RecipeIngredient> findByRecipeIdAndIsOptionalTrue(Long recipeId);
    
    /**
     * Find all required ingredients for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return a list of required ingredients for the specified recipe
     */
    List<RecipeIngredient> findByRecipeIdAndIsOptionalFalse(Long recipeId);
    
    /**
     * Find all ingredients with a specific name.
     *
     * @param name the ingredient name
     * @return a list of ingredients with the specified name
     */
    List<RecipeIngredient> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all ingredients with a specific unit.
     *
     * @param unit the unit
     * @return a list of ingredients with the specified unit
     */
    List<RecipeIngredient> findByUnit(String unit);
    
    /**
     * Delete all ingredients for a specific recipe.
     *
     * @param recipeId the recipe ID
     */
    void deleteByRecipeId(Long recipeId);
    
    /**
     * Count the number of ingredients for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return the number of ingredients for the specified recipe
     */
    @Query("SELECT COUNT(ri) FROM RecipeIngredient ri WHERE ri.recipeId = :recipeId")
    int countByRecipeId(@Param("recipeId") Long recipeId);
    
    /**
     * Find all recipes that use a specific ingredient.
     *
     * @param ingredientName the ingredient name
     * @return a list of recipe IDs that use the specified ingredient
     */
    @Query("SELECT DISTINCT ri.recipeId FROM RecipeIngredient ri WHERE LOWER(ri.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
    List<Long> findRecipeIdsByIngredientName(@Param("ingredientName") String ingredientName);
}