package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.ProductionBatch;
import com.aurelius.tech.mybakery.model.ProductionBatch.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing production batches.
 */
@Repository
public interface ProductionBatchRepository extends JpaRepository<ProductionBatch, Long> {
    
    /**
     * Find a production batch by batch number.
     *
     * @param batchNumber the batch number
     * @return the production batch with the specified batch number
     */
    Optional<ProductionBatch> findByBatchNumber(String batchNumber);
    
    /**
     * Find all production batches for a specific recipe.
     *
     * @param recipeId the recipe ID
     * @return a list of production batches for the specified recipe
     */
    List<ProductionBatch> findByRecipeId(Long recipeId);
    
    /**
     * Find all production batches for a specific product.
     *
     * @param productId the product ID
     * @return a list of production batches for the specified product
     */
    List<ProductionBatch> findByProductId(Long productId);
    
    /**
     * Find all production batches with a specific status.
     *
     * @param status the batch status
     * @return a list of production batches with the specified status
     */
    List<ProductionBatch> findByStatus(BatchStatus status);
    
    /**
     * Find all production batches assigned to a specific staff member.
     *
     * @param staffId the staff ID
     * @return a list of production batches assigned to the specified staff member
     */
    List<ProductionBatch> findByAssignedStaffId(Long staffId);
    
    /**
     * Find all production batches scheduled to start within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of production batches scheduled to start within the specified date range
     */
    List<ProductionBatch> findByScheduledStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all production batches scheduled to end within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of production batches scheduled to end within the specified date range
     */
    List<ProductionBatch> findByScheduledEndTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all production batches that actually started within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of production batches that actually started within the specified date range
     */
    List<ProductionBatch> findByActualStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all production batches that actually ended within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of production batches that actually ended within the specified date range
     */
    List<ProductionBatch> findByActualEndTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all production batches that passed quality check.
     *
     * @return a list of production batches that passed quality check
     */
    List<ProductionBatch> findByQualityCheckPassedTrue();
    
    /**
     * Find all production batches that failed quality check.
     *
     * @return a list of production batches that failed quality check
     */
    List<ProductionBatch> findByQualityCheckPassedFalse();
    
    /**
     * Find all production batches scheduled for today.
     *
     * @param startOfDay the start of the day
     * @param endOfDay the end of the day
     * @return a list of production batches scheduled for today
     */
    @Query("SELECT pb FROM ProductionBatch pb WHERE pb.scheduledStartTime BETWEEN :startOfDay AND :endOfDay")
    List<ProductionBatch> findBatchesScheduledForToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Find all production batches that are overdue (scheduled end time is in the past but status is not COMPLETED or CANCELLED).
     *
     * @param currentTime the current time
     * @return a list of overdue production batches
     */
    @Query("SELECT pb FROM ProductionBatch pb WHERE pb.scheduledEndTime < :currentTime AND pb.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<ProductionBatch> findOverdueBatches(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find all production batches for a specific product and status.
     *
     * @param productId the product ID
     * @param status the batch status
     * @return a list of production batches for the specified product and status
     */
    List<ProductionBatch> findByProductIdAndStatus(Long productId, BatchStatus status);
    
    /**
     * Find all production batches for a specific recipe and status.
     *
     * @param recipeId the recipe ID
     * @param status the batch status
     * @return a list of production batches for the specified recipe and status
     */
    List<ProductionBatch> findByRecipeIdAndStatus(Long recipeId, BatchStatus status);
    
    /**
     * Calculate the total waste quantity for a specific product.
     *
     * @param productId the product ID
     * @return the total waste quantity for the specified product
     */
    @Query("SELECT COALESCE(SUM(pb.wasteQuantity), 0) FROM ProductionBatch pb WHERE pb.productId = :productId AND pb.wasteQuantity IS NOT NULL")
    Integer calculateTotalWasteForProduct(@Param("productId") Long productId);
}