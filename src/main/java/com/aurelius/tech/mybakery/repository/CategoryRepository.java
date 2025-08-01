package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Provides methods for CRUD operations on Category entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Save a category.
     *
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);
    
    /**
     * Find a category by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the category if found, or empty if not found
     */
    Optional<Category> findById(Long id);
    
    /**
     * Find all categories.
     *
     * @return a list of all categories
     */
    List<Category> findAll();
    
    /**
     * Delete a category.
     *
     * @param category the category to delete
     */
    void delete(Category category);
    
    /**
     * Delete a category by ID.
     *
     * @param id the ID of the category to delete
     */
    void deleteById(Long id);
    
    /**
     * Find categories by parent ID.
     *
     * @param parent the parent category to search for
     * @return a list of categories with the given parent
     */
    List<Category> findByParent(Category parent);
    
    /**
     * Find active categories.
     *
     * @return a list of active categories
     */
    List<Category> findByActiveTrue();
    
    /**
     * Find categories by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @return a list of categories with names containing the given string
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find root categories (categories with no parent).
     *
     * @return a list of root categories
     */
    List<Category> findByParentIsNull();
    
    /**
     * Find categories ordered by sort order.
     *
     * @return a list of categories ordered by sort order
     */
    List<Category> findAllByOrderBySortOrderAsc();
}