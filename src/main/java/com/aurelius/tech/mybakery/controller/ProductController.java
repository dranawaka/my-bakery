package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling product-related endpoints.
 * This is a simplified version without Spring Security dependencies.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Constructor with dependencies.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Get all products.
     *
     * @return a response entity with all products
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return createSuccessResponse("Products retrieved successfully", products);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return a response entity with the product
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return createSuccessResponse("Product retrieved successfully", product);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Create a new product.
     *
     * @param product the product to create
     * @return a response entity with the created product
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return createSuccessResponse("Product created successfully", createdProduct);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Update a product.
     *
     * @param id the product ID
     * @param product the updated product information
     * @return a response entity with the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            System.out.println("Updating product with ID: " + id);
            System.out.println("Product data: " + product.toString());
            
            Product updatedProduct = productService.updateProduct(id, product);
            return createSuccessResponse("Product updated successfully", updatedProduct);
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a product.
     *
     * @param id the product ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return createSuccessResponse("Product deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Search products by name.
     *
     * @param name the name to search for
     * @return a response entity with the matching products
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProductsByName(@RequestParam String name) {
        try {
            List<Product> products = productService.searchProductsByName(name);
            return createSuccessResponse("Products retrieved successfully", products);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get products by category ID.
     *
     * @param categoryId the category ID
     * @return a response entity with the matching products
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            return createSuccessResponse("Products retrieved successfully", products);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get active products.
     *
     * @return a response entity with the active products
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveProducts() {
        try {
            List<Product> products = productService.getActiveProducts();
            return createSuccessResponse("Products retrieved successfully", products);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get featured products.
     *
     * @return a response entity with the featured products
     */
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedProducts() {
        try {
            List<Product> products = productService.getFeaturedProducts();
            return createSuccessResponse("Products retrieved successfully", products);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a product by SKU.
     *
     * @param sku the product SKU
     * @return a response entity with the product
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<?> getProductBySku(@PathVariable String sku) {
        try {
            Product product = productService.getProductBySku(sku);
            return createSuccessResponse("Product retrieved successfully", product);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Get a product by barcode.
     *
     * @param barcode the product barcode
     * @return a response entity with the product
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode) {
        try {
            Product product = productService.getProductByBarcode(barcode);
            return createSuccessResponse("Product retrieved successfully", product);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Activate or deactivate a product.
     *
     * @param id the product ID
     * @param active whether the product should be active
     * @return a response entity with the updated product
     */
    @PutMapping("/{id}/active")
    public ResponseEntity<?> setProductActive(@PathVariable Long id, @RequestParam boolean active) {
        try {
            Product product = productService.setProductActive(id, active);
            return createSuccessResponse("Product updated successfully", product);
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