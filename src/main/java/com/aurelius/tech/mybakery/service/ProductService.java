package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Category;
import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.repository.CategoryRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling product-related operations.
 */
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Constructor with dependencies.
     */
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Create a new product.
     *
     * @param product the product to create
     * @return the created product
     */
    public Product createProduct(Product product) {
        // Validate category if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
            if (categoryOptional.isPresent()) {
                product.setCategory(categoryOptional.get());
            }
        }
    
        // Set default values
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    
        return productRepository.save(product);
    }
    
    /**
     * Get a product by ID.
     *
     * @param id the product's ID
     * @return the product
     * @throws RuntimeException if the product is not found
     */
    public Product getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        return productOptional.get();
    }
    
    /**
     * Get a product by SKU.
     *
     * @param sku the product's SKU
     * @return the product
     * @throws RuntimeException if the product is not found
     */
    public Product getProductBySku(String sku) {
        Optional<Product> productOptional = productRepository.findBySku(sku);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        return productOptional.get();
    }
    
    /**
     * Get a product by barcode.
     *
     * @param barcode the product's barcode
     * @return the product
     * @throws RuntimeException if the product is not found
     */
    public Product getProductByBarcode(String barcode) {
        Optional<Product> productOptional = productRepository.findByBarcode(barcode);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        return productOptional.get();
    }
    
    /**
     * Get all products.
     *
     * @return a list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Get active products.
     *
     * @return a list of active products
     */
    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }
    
    /**
     * Get products by category ID.
     *
     * @param categoryId the category ID
     * @return a list of products with the given category ID
     */
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    /**
     * Search products by name.
     *
     * @param name the name to search for
     * @return a list of products with names containing the given string
     */
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get featured products.
     *
     * @return a list of featured products
     */
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }
    
    /**
     * Update a product.
     *
     * @param id the product's ID
     * @param updatedProduct the updated product information
     * @return the updated product
     * @throws RuntimeException if the product is not found
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                throw new RuntimeException("Product not found with ID: " + id);
            }
            
            Product existingProduct = productOptional.get();
            
            // Update fields
            if (updatedProduct.getName() != null) {
                existingProduct.setName(updatedProduct.getName());
            }
            
            if (updatedProduct.getDescription() != null) {
                existingProduct.setDescription(updatedProduct.getDescription());
            }
            
            if (updatedProduct.getShortDescription() != null) {
                existingProduct.setShortDescription(updatedProduct.getShortDescription());
            }
            
            // Handle category update - check for both category object and categoryId
            if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
                // Update category reference
                Optional<Category> categoryOptional = categoryRepository.findById(updatedProduct.getCategory().getId());
                if (categoryOptional.isPresent()) {
                    existingProduct.setCategory(categoryOptional.get());
                } else {
                    throw new RuntimeException("Category not found with ID: " + updatedProduct.getCategory().getId());
                }
            }
            
            if (updatedProduct.getPrice() != null) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            
            if (updatedProduct.getCostPrice() != null) {
                existingProduct.setCostPrice(updatedProduct.getCostPrice());
            }
            
            if (updatedProduct.getOriginalPrice() != null) {
                existingProduct.setOriginalPrice(updatedProduct.getOriginalPrice());
            }
            
            if (updatedProduct.getDiscount() != null) {
                existingProduct.setDiscount(updatedProduct.getDiscount());
            }
            
            if (updatedProduct.getSku() != null) {
                existingProduct.setSku(updatedProduct.getSku());
            }
            
            if (updatedProduct.getBarcode() != null) {
                existingProduct.setBarcode(updatedProduct.getBarcode());
            }
            
            if (updatedProduct.getImageUrl() != null) {
                existingProduct.setImageUrl(updatedProduct.getImageUrl());
            }
            
            if (updatedProduct.getImages() != null && !updatedProduct.getImages().isEmpty()) {
                existingProduct.setImages(updatedProduct.getImages());
            }
            
            if (updatedProduct.getNutritionalInfo() != null) {
                existingProduct.setNutritionalInfo(updatedProduct.getNutritionalInfo());
            }
            
            if (updatedProduct.getAllergens() != null) {
                existingProduct.setAllergens(updatedProduct.getAllergens());
            }
            
            if (updatedProduct.getIngredients() != null) {
                existingProduct.setIngredients(updatedProduct.getIngredients());
            }
            
            if (updatedProduct.getPreparationTime() != null) {
                existingProduct.setPreparationTime(updatedProduct.getPreparationTime());
            }
            
            if (updatedProduct.getShelfLife() != null) {
                existingProduct.setShelfLife(updatedProduct.getShelfLife());
            }
            
            if (updatedProduct.getStorageRequirements() != null) {
                existingProduct.setStorageRequirements(updatedProduct.getStorageRequirements());
            }
            
            if (updatedProduct.getStockQuantity() != null) {
                existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
            }
            
            existingProduct.setActive(updatedProduct.isActive());
            existingProduct.setFeatured(updatedProduct.isFeatured());
            existingProduct.setUpdatedAt(LocalDateTime.now());
            
            return productRepository.save(existingProduct);
        } catch (Exception e) {
            System.err.println("Error updating product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }
    
    /**
     * Delete a product.
     *
     * @param id the product's ID
     * @throws RuntimeException if the product is not found
     */
    public void deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        productRepository.deleteById(id);
    }
    
    /**
     * Activate or deactivate a product.
     *
     * @param id the product's ID
     * @param active whether the product should be active
     * @return the updated product
     * @throws RuntimeException if the product is not found
     */
    public Product setProductActive(Long id, boolean active) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = productOptional.get();
        product.setActive(active);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    /**
     * Set a product as featured or not.
     *
     * @param id the product's ID
     * @param featured whether the product should be featured
     * @return the updated product
     * @throws RuntimeException if the product is not found
     */
    public Product setProductFeatured(Long id, boolean featured) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = productOptional.get();
        product.setFeatured(featured);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
}