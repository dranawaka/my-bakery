package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Provides methods for CRUD operations on Product entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Save a product.
     *
     * @param product the product to save
     * @return the saved product
     */
    Product save(Product product);
    
    /**
     * Find a product by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the product if found, or empty if not found
     */
    Optional<Product> findById(Long id);
    
    /**
     * Find all products.
     *
     * @return a list of all products
     */
    List<Product> findAll();
    
    /**
     * Delete a product.
     *
     * @param product the product to delete
     */
    void delete(Product product);
    
    /**
     * Delete a product by ID.
     *
     * @param id the ID of the product to delete
     */
    void deleteById(Long id);
    
    /**
     * Find products by category ID.
     *
     * @param categoryId the category ID to search for
     * @return a list of products with the given category ID
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * Find active products.
     *
     * @return a list of active products
     */
    List<Product> findByActiveTrue();
    
    /**
     * Find products by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @return a list of products with names containing the given string
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find featured products.
     *
     * @return a list of featured products
     */
    List<Product> findByFeaturedTrue();
    
    /**
     * Find products by SKU.
     *
     * @param sku the SKU to search for
     * @return an Optional containing the product if found, or empty if not found
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Find products by barcode.
     *
     * @param barcode the barcode to search for
     * @return an Optional containing the product if found, or empty if not found
     */
    Optional<Product> findByBarcode(String barcode);
}