package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Inventory;
import com.aurelius.tech.mybakery.model.Inventory.InventoryStatus;
import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.repository.InventoryRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling inventory-related operations.
 */
@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    
    /**
     * Constructor with dependencies.
     */
    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }
    
    /**
     * Get inventory by ID.
     *
     * @param id the inventory ID
     * @return the inventory
     * @throws RuntimeException if the inventory is not found
     */
    public Inventory getInventoryById(Long id) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found");
        }
        
        return inventoryOptional.get();
    }
    
    /**
     * Get inventory by product ID.
     *
     * @param productId the product ID
     * @return the inventory
     * @throws RuntimeException if the inventory is not found
     */
    public Inventory getInventoryByProductId(Long productId) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct_Id(productId);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found for product ID: " + productId);
        }
        
        return inventoryOptional.get();
    }
    
    /**
     * Get all inventory records.
     *
     * @return a list of all inventory records
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    /**
     * Get low stock inventory.
     *
     * @return a list of inventory records with quantity less than or equal to reorder point
     */
    public List<Inventory> getLowStockInventory() {
        return inventoryRepository.findByQuantityLessThanOrEqualToReorderPoint();
    }
    
    /**
     * Get out of stock inventory.
     *
     * @return a list of inventory records with quantity equal to zero
     */
    public List<Inventory> getOutOfStockInventory() {
        return inventoryRepository.findByQuantityEquals(0);
    }
    
    /**
     * Get expiring inventory.
     *
     * @param date the date to compare with
     * @return a list of inventory records with expiry date before the given date
     */
    public List<Inventory> getExpiringInventory(LocalDateTime date) {
        return inventoryRepository.findByExpiryDateBefore(date);
    }
    
    /**
     * Create a new inventory record.
     *
     * @param inventory the inventory record to create
     * @return the created inventory record
     * @throws RuntimeException if the product is not found
     */
    public Inventory createInventory(Inventory inventory) {
        // Validate product
        if (inventory.getProductId() != null) {
            Optional<Product> productOptional = productRepository.findById(inventory.getProductId());
            if (productOptional.isEmpty()) {
                throw new RuntimeException("Product not found");
            }
            inventory.setProduct(productOptional.get());
        }
        
        // Set status based on quantity and reorder point
        inventory.setStatus(inventory.determineStatus());
        
        // Set last updated
        inventory.setLastUpdated(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Update inventory quantity.
     *
     * @param id the inventory ID
     * @param quantity the new quantity
     * @return the updated inventory record
     * @throws RuntimeException if the inventory is not found
     */
    public Inventory updateInventoryQuantity(Long id, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found");
        }
        
        Inventory inventory = inventoryOptional.get();
        inventory.setQuantity(quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Update inventory by product ID.
     *
     * @param productId the product ID
     * @param quantity the new quantity
     * @return the updated inventory record
     * @throws RuntimeException if the inventory is not found
     */
    public Inventory updateInventoryByProductId(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct_Id(productId);
        if (inventoryOptional.isEmpty()) {
            // Create new inventory record if not found
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity);
            newInventory.setReorderPoint(10); // Default reorder point
            newInventory.setReorderQuantity(20); // Default reorder quantity
            newInventory.setLastUpdated(LocalDateTime.now());
            
            return createInventory(newInventory);
        }
        
        // Use the repository's updateQuantity method
        int rowsAffected = inventoryRepository.updateQuantity(productId, quantity);
        
        if (rowsAffected > 0) {
            // Fetch and return the updated inventory
            return inventoryRepository.findByProduct_Id(productId).orElseThrow(() -> 
                new RuntimeException("Inventory not found after update for product ID: " + productId));
        } else {
            throw new RuntimeException("Failed to update inventory for product ID: " + productId);
        }
    }
    
    /**
     * Increase inventory quantity.
     *
     * @param productId the product ID
     * @param quantity the quantity to increase by
     * @return the updated inventory record
     * @throws RuntimeException if the inventory is not found
     */
    public Inventory increaseInventory(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct_Id(productId);
        if (inventoryOptional.isEmpty()) {
            // Create new inventory record if not found
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity);
            newInventory.setReorderPoint(10); // Default reorder point
            newInventory.setReorderQuantity(20); // Default reorder quantity
            newInventory.setLastUpdated(LocalDateTime.now());
            
            return createInventory(newInventory);
        }
        
        Inventory inventory = inventoryOptional.get();
        Integer currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        inventory.setQuantity(currentQuantity + quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Decrease inventory quantity.
     *
     * @param productId the product ID
     * @param quantity the quantity to decrease by
     * @return the updated inventory record
     * @throws RuntimeException if the inventory is not found or insufficient stock
     */
    public Inventory decreaseInventory(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct_Id(productId);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found for product ID: " + productId);
        }
        
        Inventory inventory = inventoryOptional.get();
        Integer currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        
        if (currentQuantity < quantity) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }
        
        inventory.setQuantity(currentQuantity - quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Check if a product is in stock.
     *
     * @param productId the product ID
     * @param quantity the quantity to check
     * @return true if the product is in stock, false otherwise
     */
    public boolean isInStock(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct_Id(productId);
        if (inventoryOptional.isEmpty()) {
            return false;
        }
        
        Inventory inventory = inventoryOptional.get();
        Integer currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        
        return currentQuantity >= quantity;
    }
    
    /**
     * Delete an inventory record.
     *
     * @param id the inventory ID
     * @throws RuntimeException if the inventory is not found
     */
    public void deleteInventory(Long id) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
        if (inventoryOptional.isEmpty()) {
            throw new RuntimeException("Inventory not found");
        }
        
        inventoryRepository.deleteById(id);
    }
}