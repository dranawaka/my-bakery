package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Recipe;
import com.aurelius.tech.mybakery.model.Recipe.DifficultyLevel;
import com.aurelius.tech.mybakery.model.RecipeIngredient;

import java.util.List;

/**
 * Service interface for managing recipes.
 */
public interface RecipeService {
    
    /**
     * Get all recipes.
     *
     * @return all recipes
     */
    List<Recipe> getAllRecipes();
    
    /**
     * Get all active recipes.
     *
     * @return all active recipes
     */
    List<Recipe> getActiveRecipes();
    
    /**
     * Get a recipe by ID.
     *
     * @param id the recipe ID
     * @return the recipe with the specified ID
     */
    Recipe getRecipeById(Long id);
    
    /**
     * Get a recipe by name.
     *
     * @param name the recipe name
     * @return the recipe with the specified name
     */
    Recipe getRecipeByName(String name);
    
    /**
     * Get all recipes for a specific product.
     *
     * @param productId the product ID
     * @return all recipes for the specified product
     */
    List<Recipe> getRecipesByProductId(Long productId);
    
    /**
     * Get all active recipes for a specific product.
     *
     * @param productId the product ID
     * @return all active recipes for the specified product
     */
    List<Recipe> getActiveRecipesByProductId(Long productId);
    
    /**
     * Get all recipes with a specific difficulty level.
     *
     * @param difficultyLevel the difficulty level
     * @return all recipes with the specified difficulty level
     */
    List<Recipe> getRecipesByDifficultyLevel(DifficultyLevel difficultyLevel);
    
    /**
     * Get all recipes with a preparation time less than or equal to the specified value.
     *
     * @param preparationTime the maximum preparation time
     * @return all recipes with a preparation time less than or equal to the specified value
     */
    List<Recipe> getRecipesByPreparationTime(Integer preparationTime);
    
    /**
     * Get all recipes with a total time less than or equal to the specified value.
     *
     * @param totalTime the maximum total time
     * @return all recipes with a total time less than or equal to the specified value
     */
    List<Recipe> getRecipesByTotalTime(Integer totalTime);
    
    /**
     * Search for recipes by name or description.
     *
     * @param searchTerm the search term
     * @return all recipes matching the search term
     */
    List<Recipe> searchRecipes(String searchTerm);
    
    /**
     * Find all recipes that contain a specific ingredient.
     *
     * @param ingredientName the ingredient name
     * @return all recipes containing the specified ingredient
     */
    List<Recipe> getRecipesByIngredient(String ingredientName);
    
    /**
     * Create a new recipe.
     *
     * @param recipe the recipe to create
     * @return the created recipe
     */
    Recipe createRecipe(Recipe recipe);
    
    /**
     * Update an existing recipe.
     *
     * @param id the recipe ID
     * @param recipe the updated recipe
     * @return the updated recipe
     */
    Recipe updateRecipe(Long id, Recipe recipe);
    
    /**
     * Delete a recipe.
     *
     * @param id the recipe ID
     */
    void deleteRecipe(Long id);
    
    /**
     * Activate a recipe.
     *
     * @param id the recipe ID
     * @return the activated recipe
     */
    Recipe activateRecipe(Long id);
    
    /**
     * Deactivate a recipe.
     *
     * @param id the recipe ID
     * @return the deactivated recipe
     */
    Recipe deactivateRecipe(Long id);
    
    /**
     * Get all ingredients for a recipe.
     *
     * @param recipeId the recipe ID
     * @return all ingredients for the specified recipe
     */
    List<RecipeIngredient> getRecipeIngredients(Long recipeId);
    
    /**
     * Add an ingredient to a recipe.
     *
     * @param recipeId the recipe ID
     * @param ingredient the ingredient to add
     * @return the added ingredient
     */
    RecipeIngredient addIngredientToRecipe(Long recipeId, RecipeIngredient ingredient);
    
    /**
     * Update an ingredient in a recipe.
     *
     * @param id the ingredient ID
     * @param ingredient the updated ingredient
     * @return the updated ingredient
     */
    RecipeIngredient updateRecipeIngredient(Long id, RecipeIngredient ingredient);
    
    /**
     * Remove an ingredient from a recipe.
     *
     * @param recipeId the recipe ID
     * @param ingredientId the ingredient ID
     */
    void removeIngredientFromRecipe(Long recipeId, Long ingredientId);
    
    /**
     * Get all optional ingredients for a recipe.
     *
     * @param recipeId the recipe ID
     * @return all optional ingredients for the specified recipe
     */
    List<RecipeIngredient> getOptionalIngredients(Long recipeId);
    
    /**
     * Get all required ingredients for a recipe.
     *
     * @param recipeId the recipe ID
     * @return all required ingredients for the specified recipe
     */
    List<RecipeIngredient> getRequiredIngredients(Long recipeId);
}