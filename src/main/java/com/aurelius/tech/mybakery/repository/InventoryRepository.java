package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Inventory;
import com.aurelius.tech.mybakery.model.Inventory.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory entity.
 * Provides methods for CRUD operations on Inventory entities.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Save an inventory record.
     *
     * @param inventory the inventory record to save
     * @return the saved inventory record
     */
    Inventory save(Inventory inventory);
    
    /**
     * Find an inventory record by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the inventory record if found, or empty if not found
     */
    Optional<Inventory> findById(Long id);
    
    /**
     * Find an inventory record by product ID.
     *
     * @param productId the product ID to search for
     * @return an Optional containing the inventory record if found, or empty if not found
     */
    Optional<Inventory> findByProduct_Id(Long productId);
    
    /**
     * Find all inventory records.
     *
     * @return a list of all inventory records
     */
    List<Inventory> findAll();
    
    /**
     * Delete an inventory record.
     *
     * @param inventory the inventory record to delete
     */
    void delete(Inventory inventory);
    
    /**
     * Delete an inventory record by ID.
     *
     * @param id the ID of the inventory record to delete
     */
    void deleteById(Long id);
    
    /**
     * Find inventory records with quantity less than or equal to reorder point.
     *
     * @return a list of inventory records with quantity less than or equal to reorder point
     */
    @org.springframework.data.jpa.repository.Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderPoint")
    List<Inventory> findByQuantityLessThanOrEqualToReorderPoint();
    
    /**
     * Find inventory records by status.
     *
     * @param status the status to search for
     * @return a list of inventory records with the given status
     */
    List<Inventory> findByStatus(InventoryStatus status);
    
    /**
     * Find inventory records with expiry date before the given date.
     *
     * @param date the date to compare with
     * @return a list of inventory records with expiry date before the given date
     */
    List<Inventory> findByExpiryDateBefore(LocalDateTime date);
    
    /**
     * Find inventory records by batch number.
     *
     * @param batchNumber the batch number to search for
     * @return a list of inventory records with the given batch number
     */
    List<Inventory> findByBatchNumber(String batchNumber);
    
    /**
     * Find inventory records by location.
     *
     * @param location the location to search for
     * @return a list of inventory records with the given location
     */
    List<Inventory> findByLocation(String location);
    
    /**
     * Find inventory records with quantity equal to zero.
     *
     * @return a list of inventory records with quantity equal to zero
     */
    List<Inventory> findByQuantityEquals(Integer quantity);
    
    /**
     * Update inventory quantity.
     *
     * @param productId the product ID to update
     * @param quantity the new quantity
     * @return the number of rows affected
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Inventory i SET i.quantity = :quantity, i.lastUpdated = CURRENT_TIMESTAMP, i.status = CASE WHEN :quantity <= 0 THEN 'OUT_OF_STOCK' WHEN :quantity < i.reorderPoint THEN 'LOW_STOCK' ELSE 'IN_STOCK' END WHERE i.product.id = :productId")
    @org.springframework.transaction.annotation.Transactional
    int updateQuantity(Long productId, Integer quantity);
}