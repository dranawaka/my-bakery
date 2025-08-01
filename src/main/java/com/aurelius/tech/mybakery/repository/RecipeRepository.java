package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Recipe;
import com.aurelius.tech.mybakery.model.Recipe.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing recipes.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    /**
     * Find all active recipes.
     *
     * @return a list of active recipes
     */
    List<Recipe> findByIsActiveTrue();
    
    /**
     * Find a recipe by name.
     *
     * @param name the recipe name
     * @return the recipe with the specified name
     */
    Optional<Recipe> findByName(String name);
    
    /**
     * Find all recipes for a specific product.
     *
     * @param productId the product ID
     * @return a list of recipes for the specified product
     */
    List<Recipe> findByProductId(Long productId);
    
    /**
     * Find all active recipes for a specific product.
     *
     * @param productId the product ID
     * @return a list of active recipes for the specified product
     */
    List<Recipe> findByProductIdAndIsActiveTrue(Long productId);
    
    /**
     * Find all recipes with a specific difficulty level.
     *
     * @param difficultyLevel the difficulty level
     * @return a list of recipes with the specified difficulty level
     */
    List<Recipe> findByDifficultyLevel(DifficultyLevel difficultyLevel);
    
    /**
     * Find all recipes with a preparation time less than or equal to the specified value.
     *
     * @param preparationTime the maximum preparation time
     * @return a list of recipes with a preparation time less than or equal to the specified value
     */
    List<Recipe> findByPreparationTimeLessThanEqual(Integer preparationTime);
    
    /**
     * Find all recipes with a total time less than or equal to the specified value.
     *
     * @param totalTime the maximum total time
     * @return a list of recipes with a total time less than or equal to the specified value
     */
    List<Recipe> findByTotalTimeLessThanEqual(Integer totalTime);
    
    /**
     * Search for recipes by name or description.
     *
     * @param searchTerm the search term
     * @return a list of recipes matching the search term
     */
    @Query("SELECT r FROM Recipe r WHERE r.isActive = true AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Recipe> searchRecipes(@Param("searchTerm") String searchTerm);
    
    /**
     * Find all recipes that contain a specific ingredient.
     *
     * @param ingredientName the ingredient name
     * @return a list of recipes containing the specified ingredient
     */
    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE r.isActive = true AND LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
    List<Recipe> findByIngredientName(@Param("ingredientName") String ingredientName);
}