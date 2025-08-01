package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Category;
import com.aurelius.tech.mybakery.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling category-related operations.
 */
@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Constructor with dependencies.
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Create a new category.
     *
     * @param category the category to create
     * @return the created category
     */
    public Category createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return the category
     * @throws RuntimeException if the category is not found
     */
    public Category getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        
        return categoryOptional.get();
    }
    
    /**
     * Get all categories.
     *
     * @return a list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * Get active categories.
     *
     * @return a list of active categories
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }
    
    /**
     * Get categories by parent ID.
     *
     * @param parentId the parent ID
     * @return a list of categories with the given parent ID
     */
    public List<Category> getCategoriesByParentId(Long parentId) {
        if (parentId == null) {
            return categoryRepository.findByParentIsNull();
        }
        
        // Find the parent category first
        Optional<Category> parentCategory = categoryRepository.findById(parentId);
        if (parentCategory.isEmpty()) {
            return List.of(); // Return empty list if parent doesn't exist
        }
        
        return categoryRepository.findByParent(parentCategory.get());
    }
    
    /**
     * Get root categories (categories with no parent).
     *
     * @return a list of root categories
     */
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }
    
    /**
     * Search categories by name.
     *
     * @param name the name to search for
     * @return a list of categories with names containing the given string
     */
    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Update a category.
     *
     * @param id the category ID
     * @param updatedCategory the updated category information
     * @return the updated category
     * @throws RuntimeException if the category is not found
     */
    public Category updateCategory(Long id, Category updatedCategory) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        
        Category existingCategory = categoryOptional.get();
        
        // Update fields
        if (updatedCategory.getName() != null) {
            existingCategory.setName(updatedCategory.getName());
        }
        
        if (updatedCategory.getDescription() != null) {
            existingCategory.setDescription(updatedCategory.getDescription());
        }
        
        if (updatedCategory.getIcon() != null) {
            existingCategory.setIcon(updatedCategory.getIcon());
        }
        
        if (updatedCategory.getParentId() != null) {
            existingCategory.setParentId(updatedCategory.getParentId());
            
            // Update parent reference
            Optional<Category> parentOptional = categoryRepository.findById(updatedCategory.getParentId());
            parentOptional.ifPresent(existingCategory::setParent);
        }
        
        if (updatedCategory.getSortOrder() != null) {
            existingCategory.setSortOrder(updatedCategory.getSortOrder());
        }
        
        existingCategory.setActive(updatedCategory.isActive());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(existingCategory);
    }
    
    /**
     * Delete a category.
     *
     * @param id the category ID
     * @throws RuntimeException if the category is not found
     */
    public void deleteCategory(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        
        categoryRepository.deleteById(id);
    }
    
    /**
     * Activate or deactivate a category.
     *
     * @param id the category ID
     * @param active whether the category should be active
     * @return the updated category
     * @throws RuntimeException if the category is not found
     */
    public Category setCategoryActive(Long id, boolean active) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        
        Category category = categoryOptional.get();
        category.setActive(active);
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
}