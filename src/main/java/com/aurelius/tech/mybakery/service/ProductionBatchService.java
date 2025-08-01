package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.ProductionBatch;
import com.aurelius.tech.mybakery.model.ProductionBatch.BatchStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing production batches.
 */
public interface ProductionBatchService {
    
    /**
     * Get all production batches.
     *
     * @return all production batches
     */
    List<ProductionBatch> getAllBatches();
    
    /**
     * Get a production batch by ID.
     *
     * @param id the batch ID
     * @return the production batch with the specified ID
     */
    ProductionBatch getBatchById(Long id);
    
    /**
     * Get a production batch by batch number.
     *
     * @param batchNumber the batch number
     * @return the production batch with the specified batch number
     */
    ProductionBatch getBatchByBatchNumber(String batchNumber);
    
    /**
     * Get all production batches for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return all production batches for the specified recipe
     */
    List<ProductionBatch> getBatchesByRecipeId(Long recipeId);
    
    /**
     * Get all production batches for a specific product.
     *
     * @param productId the product ID
     * @return all production batches for the specified product
     */
    List<ProductionBatch> getBatchesByProductId(Long productId);
    
    /**
     * Get all production batches with a specific status.
     *
     * @param status the batch status
     * @return all production batches with the specified status
     */
    List<ProductionBatch> getBatchesByStatus(BatchStatus status);
    
    /**
     * Get all production batches assigned to a specific staff member.
     *
     * @param staffId the staff ID
     * @return all production batches assigned to the specified staff member
     */
    List<ProductionBatch> getBatchesByStaffId(Long staffId);
    
    /**
     * Get all production batches scheduled for a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return all production batches scheduled for the specified date range
     */
    List<ProductionBatch> getBatchesByScheduledDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all production batches scheduled for today.
     *
     * @return all production batches scheduled for today
     */
    List<ProductionBatch> getBatchesScheduledForToday();
    
    /**
     * Get all overdue production batches.
     *
     * @return all overdue production batches
     */
    List<ProductionBatch> getOverdueBatches();
    
    /**
     * Create a new production batch.
     *
     * @param batch the production batch to create
     * @return the created production batch
     */
    ProductionBatch createBatch(ProductionBatch batch);
    
    /**
     * Update an existing production batch.
     *
     * @param id the batch ID
     * @param batch the updated production batch
     * @return the updated production batch
     */
    ProductionBatch updateBatch(Long id, ProductionBatch batch);
    
    /**
     * Delete a production batch.
     *
     * @param id the batch ID
     */
    void deleteBatch(Long id);
    
    /**
     * Start a production batch.
     *
     * @param id the batch ID
     * @return the started production batch
     */
    ProductionBatch startBatch(Long id);
    
    /**
     * Complete a production batch.
     *
     * @param id the batch ID
     * @return the completed production batch
     */
    ProductionBatch completeBatch(Long id);
    
    /**
     * Cancel a production batch.
     *
     * @param id the batch ID
     * @return the cancelled production batch
     */
    ProductionBatch cancelBatch(Long id);
    
    /**
     * Perform quality check on a production batch.
     *
     * @param id the batch ID
     * @param passed whether the quality check passed
     * @param notes quality check notes
     * @param wasteQuantity quantity of waste
     * @return the production batch with quality check performed
     */
    ProductionBatch performQualityCheck(Long id, boolean passed, String notes, Integer wasteQuantity);
    
    /**
     * Assign a staff member to a production batch.
     *
     * @param batchId the batch ID
     * @param staffId the staff ID
     * @return the production batch with staff assigned
     */
    ProductionBatch assignStaffToBatch(Long batchId, Long staffId);
    
    /**
     * Update the equipment used for a production batch.
     *
     * @param batchId the batch ID
     * @param equipment the equipment used
     * @return the production batch with equipment updated
     */
    ProductionBatch updateBatchEquipment(Long batchId, String equipment);
    
    /**
     * Calculate the total waste quantity for a specific product.
     *
     * @param productId the product ID
     * @return the total waste quantity for the specified product
     */
    Integer calculateTotalWasteForProduct(Long productId);
}