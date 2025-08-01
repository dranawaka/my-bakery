package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.exception.ResourceNotFoundException;
import com.aurelius.tech.mybakery.model.Recipe;
import com.aurelius.tech.mybakery.model.Recipe.DifficultyLevel;
import com.aurelius.tech.mybakery.model.RecipeIngredient;
import com.aurelius.tech.mybakery.repository.RecipeIngredientRepository;
import com.aurelius.tech.mybakery.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the RecipeService interface.
 */
@Service
public class RecipeServiceImpl implements RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public RecipeServiceImpl(
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }
    
    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    @Override
    public List<Recipe> getActiveRecipes() {
        return recipeRepository.findByIsActiveTrue();
    }
    
    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }
    
    @Override
    public Recipe getRecipeByName(String name) {
        return recipeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with name: " + name));
    }
    
    @Override
    public List<Recipe> getRecipesByProductId(Long productId) {
        return recipeRepository.findByProductId(productId);
    }
    
    @Override
    public List<Recipe> getActiveRecipesByProductId(Long productId) {
        return recipeRepository.findByProductIdAndIsActiveTrue(productId);
    }
    
    @Override
    public List<Recipe> getRecipesByDifficultyLevel(DifficultyLevel difficultyLevel) {
        return recipeRepository.findByDifficultyLevel(difficultyLevel);
    }
    
    @Override
    public List<Recipe> getRecipesByPreparationTime(Integer preparationTime) {
        return recipeRepository.findByPreparationTimeLessThanEqual(preparationTime);
    }
    
    @Override
    public List<Recipe> getRecipesByTotalTime(Integer totalTime) {
        return recipeRepository.findByTotalTimeLessThanEqual(totalTime);
    }
    
    @Override
    public List<Recipe> searchRecipes(String searchTerm) {
        return recipeRepository.searchRecipes(searchTerm);
    }
    
    @Override
    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        return recipeRepository.findByIngredientName(ingredientName);
    }
    
    @Override
    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        // Save ingredients if provided
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipeId(savedRecipe.getId());
                recipeIngredientRepository.save(ingredient);
            }
        }
        
        return savedRecipe;
    }
    
    @Override
    @Transactional
    public Recipe updateRecipe(Long id, Recipe recipe) {
        Recipe existingRecipe = getRecipeById(id);
        
        // Update recipe fields
        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setProductId(recipe.getProductId());
        existingRecipe.setPreparationTime(recipe.getPreparationTime());
        existingRecipe.setCookingTime(recipe.getCookingTime());
        existingRecipe.setRestingTime(recipe.getRestingTime());
        existingRecipe.setServingSize(recipe.getServingSize());
        existingRecipe.setDifficultyLevel(recipe.getDifficultyLevel());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setNotes(recipe.getNotes());
        existingRecipe.setIsActive(recipe.getIsActive());
        
        return recipeRepository.save(existingRecipe);
    }
    
    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        
        // Delete all ingredients first
        recipeIngredientRepository.deleteByRecipeId(id);
        
        // Delete the recipe
        recipeRepository.delete(recipe);
    }
    
    @Override
    @Transactional
    public Recipe activateRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipe.setIsActive(true);
        return recipeRepository.save(recipe);
    }
    
    @Override
    @Transactional
    public Recipe deactivateRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipe.setIsActive(false);
        return recipeRepository.save(recipe);
    }
    
    @Override
    public List<RecipeIngredient> getRecipeIngredients(Long recipeId) {
        // Verify recipe exists
        getRecipeById(recipeId);
        
        return recipeIngredientRepository.findByRecipeIdOrderBySortOrderAsc(recipeId);
    }
    
    @Override
    @Transactional
    public RecipeIngredient addIngredientToRecipe(Long recipeId, RecipeIngredient ingredient) {
        // Verify recipe exists
        Recipe recipe = getRecipeById(recipeId);
        
        // Set recipe ID and save
        ingredient.setRecipeId(recipeId);
        
        // Set sort order if not provided
        if (ingredient.getSortOrder() == null) {
            int count = recipeIngredientRepository.countByRecipeId(recipeId);
            ingredient.setSortOrder(count + 1);
        }
        
        RecipeIngredient savedIngredient = recipeIngredientRepository.save(ingredient);
        
        // Add to recipe's ingredients list
        recipe.addIngredient(savedIngredient);
        
        return savedIngredient;
    }
    
    @Override
    @Transactional
    public RecipeIngredient updateRecipeIngredient(Long id, RecipeIngredient ingredient) {
        RecipeIngredient existingIngredient = recipeIngredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe ingredient not found with id: " + id));
        
        // Update fields
        existingIngredient.setName(ingredient.getName());
        existingIngredient.setQuantity(ingredient.getQuantity());
        existingIngredient.setUnit(ingredient.getUnit());
        existingIngredient.setNotes(ingredient.getNotes());
        existingIngredient.setIsOptional(ingredient.getIsOptional());
        existingIngredient.setSortOrder(ingredient.getSortOrder());
        
        return recipeIngredientRepository.save(existingIngredient);
    }
    
    @Override
    @Transactional
    public void removeIngredientFromRecipe(Long recipeId, Long ingredientId) {
        // Verify recipe exists
        Recipe recipe = getRecipeById(recipeId);
        
        RecipeIngredient ingredient = recipeIngredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe ingredient not found with id: " + ingredientId));
        
        // Verify ingredient belongs to recipe
        if (!ingredient.getRecipeId().equals(recipeId)) {
            throw new IllegalArgumentException("Ingredient does not belong to the specified recipe");
        }
        
        // Remove from recipe's ingredients list
        recipe.removeIngredient(ingredient);
        
        // Delete the ingredient
        recipeIngredientRepository.delete(ingredient);
    }
    
    @Override
    public List<RecipeIngredient> getOptionalIngredients(Long recipeId) {
        // Verify recipe exists
        getRecipeById(recipeId);
        
        return recipeIngredientRepository.findByRecipeIdAndIsOptionalTrue(recipeId);
    }
    
    @Override
    public List<RecipeIngredient> getRequiredIngredients(Long recipeId) {
        // Verify recipe exists
        getRecipeById(recipeId);
        
        return recipeIngredientRepository.findByRecipeIdAndIsOptionalFalse(recipeId);
    }
}