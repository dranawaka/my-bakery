package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.model.Inventory;
import com.aurelius.tech.mybakery.model.Inventory.InventoryStatus;
import com.aurelius.tech.mybakery.service.InventoryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling inventory-related endpoints.
 * This is a simplified version without Spring Security dependencies.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    /**
     * Constructor with dependencies.
     */
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * Get all inventory records.
     *
     * @return a response entity with all inventory records
     */
    @GetMapping
    public ResponseEntity<?> getAllInventory() {
        try {
            List<Inventory> inventoryList = inventoryService.getAllInventory();
            return createSuccessResponse("Inventory records retrieved successfully", inventoryList);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get an inventory record by ID.
     *
     * @param id the inventory ID
     * @return a response entity with the inventory record
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable Long id) {
        try {
            Inventory inventory = inventoryService.getInventoryById(id);
            return createSuccessResponse("Inventory record retrieved successfully", inventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Get an inventory record by product ID.
     *
     * @param productId the product ID
     * @return a response entity with the inventory record
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long productId) {
        try {
            Inventory inventory = inventoryService.getInventoryByProductId(productId);
            return createSuccessResponse("Inventory record retrieved successfully", inventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Create a new inventory record.
     *
     * @param inventory the inventory record to create
     * @return a response entity with the created inventory record
     */
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody Inventory inventory) {
        try {
            Inventory createdInventory = inventoryService.createInventory(inventory);
            return createSuccessResponse("Inventory record created successfully", createdInventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Update inventory quantity.
     *
     * @param id the inventory ID
     * @param quantity the new quantity
     * @return a response entity with the updated inventory record
     */
    @PutMapping("/{id}/quantity")
    public ResponseEntity<?> updateInventoryQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            Inventory updatedInventory = inventoryService.updateInventoryQuantity(id, quantity);
            return createSuccessResponse("Inventory quantity updated successfully", updatedInventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update inventory by product ID.
     *
     * @param productId the product ID
     * @param quantity the new quantity
     * @return a response entity with the updated inventory record
     */
    @PutMapping("/product/{productId}/quantity")
    public ResponseEntity<?> updateInventoryByProductId(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            Inventory updatedInventory = inventoryService.updateInventoryByProductId(productId, quantity);
            return createSuccessResponse("Inventory quantity updated successfully", updatedInventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Increase inventory quantity.
     *
     * @param productId the product ID
     * @param quantity the quantity to increase by
     * @return a response entity with the updated inventory record
     */
    @PutMapping("/product/{productId}/increase")
    public ResponseEntity<?> increaseInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            Inventory updatedInventory = inventoryService.increaseInventory(productId, quantity);
            return createSuccessResponse("Inventory quantity increased successfully", updatedInventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Decrease inventory quantity.
     *
     * @param productId the product ID
     * @param quantity the quantity to decrease by
     * @return a response entity with the updated inventory record
     */
    @PutMapping("/product/{productId}/decrease")
    public ResponseEntity<?> decreaseInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            Inventory updatedInventory = inventoryService.decreaseInventory(productId, quantity);
            return createSuccessResponse("Inventory quantity decreased successfully", updatedInventory);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Check if a product is in stock.
     *
     * @param productId the product ID
     * @param quantity the quantity to check
     * @return a response entity with the result
     */
    @GetMapping("/product/{productId}/in-stock")
    public ResponseEntity<?> isInStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            boolean inStock = inventoryService.isInStock(productId, quantity);
            Map<String, Boolean> result = Map.of("inStock", inStock);
            return createSuccessResponse("Stock availability checked successfully", result);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete an inventory record.
     *
     * @param id the inventory ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return createSuccessResponse("Inventory record deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get low stock inventory.
     *
     * @return a response entity with the low stock inventory records
     */
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockInventory() {
        try {
            List<Inventory> inventoryList = inventoryService.getLowStockInventory();
            
            // Transform inventory data to match frontend expectations
            List<Map<String, Object>> transformedData = inventoryList.stream()
                .map(inventory -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", inventory.getProduct().getId());
                    item.put("productName", inventory.getProduct().getName());
                    item.put("category", inventory.getProduct().getCategory() != null ? 
                        inventory.getProduct().getCategory().getName() : "Uncategorized");
                    item.put("currentStock", inventory.getQuantity());
                    item.put("minStock", inventory.getReorderPoint());
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return createSuccessResponse("Low stock inventory records retrieved successfully", transformedData);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get out of stock inventory.
     *
     * @return a response entity with the out of stock inventory records
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<?> getOutOfStockInventory() {
        try {
            List<Inventory> inventoryList = inventoryService.getOutOfStockInventory();
            return createSuccessResponse("Out of stock inventory records retrieved successfully", inventoryList);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get expiring inventory.
     *
     * @param date the date to compare with
     * @return a response entity with the expiring inventory records
     */
    @GetMapping("/expiring")
    public ResponseEntity<?> getExpiringInventory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<Inventory> inventoryList = inventoryService.getExpiringInventory(date);
            return createSuccessResponse("Expiring inventory records retrieved successfully", inventoryList);
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