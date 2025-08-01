package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.model.Category;
import com.aurelius.tech.mybakery.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling category-related endpoints.
 * This is a simplified version without Spring Security dependencies.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Constructor with dependencies.
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * Get all categories.
     *
     * @return a response entity with all categories
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return createSuccessResponse("Categories retrieved successfully", categories);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return a response entity with the category
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return createSuccessResponse("Category retrieved successfully", category);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Create a new category.
     *
     * @param category the category to create
     * @return a response entity with the created category
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return createSuccessResponse("Category created successfully", createdCategory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Update a category.
     *
     * @param id the category ID
     * @param category the updated category information
     * @return a response entity with the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return createSuccessResponse("Category updated successfully", updatedCategory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a category.
     *
     * @param id the category ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return createSuccessResponse("Category deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get categories by parent ID.
     *
     * @param parentId the parent ID
     * @return a response entity with the matching categories
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<Category> categories = categoryService.getCategoriesByParentId(parentId);
            return createSuccessResponse("Categories retrieved successfully", categories);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get root categories (categories with no parent).
     *
     * @return a response entity with the root categories
     */
    @GetMapping("/root")
    public ResponseEntity<?> getRootCategories() {
        try {
            List<Category> categories = categoryService.getRootCategories();
            return createSuccessResponse("Root categories retrieved successfully", categories);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get active categories.
     *
     * @return a response entity with the active categories
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCategories() {
        try {
            List<Category> categories = categoryService.getActiveCategories();
            return createSuccessResponse("Active categories retrieved successfully", categories);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Search categories by name.
     *
     * @param name the name to search for
     * @return a response entity with the matching categories
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCategoriesByName(@RequestParam String name) {
        try {
            List<Category> categories = categoryService.searchCategoriesByName(name);
            return createSuccessResponse("Categories retrieved successfully", categories);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Activate or deactivate a category.
     *
     * @param id the category ID
     * @param active whether the category should be active
     * @return a response entity with the updated category
     */
    @PutMapping("/{id}/active")
    public ResponseEntity<?> setCategoryActive(@PathVariable Long id, @RequestParam boolean active) {
        try {
            Category category = categoryService.setCategoryActive(id, active);
            return createSuccessResponse("Category updated successfully", category);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Create a success response.
     *
     * @param message the success message
     * @param data the response data
     * @return a response entity with the success response
     */
    private ResponseEntity<?> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create an error response.
     *
     * @param message the error message
     * @param status the HTTP status
     * @return a response entity with the error response
     */
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", Map.of("message", message));
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(status).body(response);
    }
}