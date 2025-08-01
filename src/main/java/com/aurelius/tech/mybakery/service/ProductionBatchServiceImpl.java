package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.exception.ResourceNotFoundException;
import com.aurelius.tech.mybakery.model.ProductionBatch;
import com.aurelius.tech.mybakery.model.ProductionBatch.BatchStatus;
import com.aurelius.tech.mybakery.model.Recipe;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.ProductionBatchRepository;
import com.aurelius.tech.mybakery.repository.RecipeRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementation of the ProductionBatchService interface.
 */
@Service
public class ProductionBatchServiceImpl implements ProductionBatchService {
    
    private final ProductionBatchRepository productionBatchRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public ProductionBatchServiceImpl(
            ProductionBatchRepository productionBatchRepository,
            RecipeRepository recipeRepository,
            UserRepository userRepository) {
        this.productionBatchRepository = productionBatchRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public List<ProductionBatch> getAllBatches() {
        return productionBatchRepository.findAll();
    }
    
    @Override
    public ProductionBatch getBatchById(Long id) {
        return productionBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Production batch not found with id: " + id));
    }
    
    @Override
    public ProductionBatch getBatchByBatchNumber(String batchNumber) {
        return productionBatchRepository.findByBatchNumber(batchNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Production batch not found with batch number: " + batchNumber));
    }
    
    @Override
    public List<ProductionBatch> getBatchesByRecipeId(Long recipeId) {
        // Verify recipe exists
        recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        
        return productionBatchRepository.findByRecipeId(recipeId);
    }
    
    @Override
    public List<ProductionBatch> getBatchesByProductId(Long productId) {
        return productionBatchRepository.findByProductId(productId);
    }
    
    @Override
    public List<ProductionBatch> getBatchesByStatus(BatchStatus status) {
        return productionBatchRepository.findByStatus(status);
    }
    
    @Override
    public List<ProductionBatch> getBatchesByStaffId(Long staffId) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return productionBatchRepository.findByAssignedStaffId(staffId);
    }
    
    @Override
    public List<ProductionBatch> getBatchesByScheduledDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return productionBatchRepository.findByScheduledStartTimeBetween(startDate, endDate);
    }
    
    @Override
    public List<ProductionBatch> getBatchesScheduledForToday() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        return productionBatchRepository.findBatchesScheduledForToday(startOfDay, endOfDay);
    }
    
    @Override
    public List<ProductionBatch> getOverdueBatches() {
        return productionBatchRepository.findOverdueBatches(LocalDateTime.now());
    }
    
    @Override
    @Transactional
    public ProductionBatch createBatch(ProductionBatch batch) {
        // Validate recipe if provided
        if (batch.getRecipeId() != null) {
            Recipe recipe = recipeRepository.findById(batch.getRecipeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + batch.getRecipeId()));
            
            // If product ID is not set, use the one from the recipe
            if (batch.getProductId() == null && recipe.getProductId() != null) {
                batch.setProductId(recipe.getProductId());
            }
        }
        
        // Validate staff if provided
        if (batch.getAssignedStaffId() != null) {
            userRepository.findById(batch.getAssignedStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + batch.getAssignedStaffId()));
        }
        
        // Set initial status if not provided
        if (batch.getStatus() == null) {
            batch.setStatus(BatchStatus.PLANNED);
        }
        
        return productionBatchRepository.save(batch);
    }
    
    @Override
    @Transactional
    public ProductionBatch updateBatch(Long id, ProductionBatch batch) {
        ProductionBatch existingBatch = getBatchById(id);
        
        // Validate recipe if changed
        if (batch.getRecipeId() != null && !batch.getRecipeId().equals(existingBatch.getRecipeId())) {
            recipeRepository.findById(batch.getRecipeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + batch.getRecipeId()));
        }
        
        // Validate staff if changed
        if (batch.getAssignedStaffId() != null && !batch.getAssignedStaffId().equals(existingBatch.getAssignedStaffId())) {
            userRepository.findById(batch.getAssignedStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + batch.getAssignedStaffId()));
        }
        
        // Update fields
        existingBatch.setRecipeId(batch.getRecipeId());
        existingBatch.setProductId(batch.getProductId());
        existingBatch.setQuantity(batch.getQuantity());
        existingBatch.setBatchSize(batch.getBatchSize());
        existingBatch.setStatus(batch.getStatus());
        existingBatch.setScheduledStartTime(batch.getScheduledStartTime());
        existingBatch.setScheduledEndTime(batch.getScheduledEndTime());
        existingBatch.setAssignedStaffId(batch.getAssignedStaffId());
        existingBatch.setEquipmentUsed(batch.getEquipmentUsed());
        existingBatch.setNotes(batch.getNotes());
        
        return productionBatchRepository.save(existingBatch);
    }
    
    @Override
    @Transactional
    public void deleteBatch(Long id) {
        ProductionBatch batch = getBatchById(id);
        productionBatchRepository.delete(batch);
    }
    
    @Override
    @Transactional
    public ProductionBatch startBatch(Long id) {
        ProductionBatch batch = getBatchById(id);
        
        try {
            batch.start();
            return productionBatchRepository.save(batch);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot start batch: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductionBatch completeBatch(Long id) {
        ProductionBatch batch = getBatchById(id);
        
        try {
            batch.complete();
            return productionBatchRepository.save(batch);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot complete batch: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductionBatch cancelBatch(Long id) {
        ProductionBatch batch = getBatchById(id);
        
        try {
            batch.cancel();
            return productionBatchRepository.save(batch);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot cancel batch: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductionBatch performQualityCheck(Long id, boolean passed, String notes, Integer wasteQuantity) {
        ProductionBatch batch = getBatchById(id);
        
        try {
            batch.performQualityCheck(passed, notes, wasteQuantity);
            return productionBatchRepository.save(batch);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot perform quality check: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductionBatch assignStaffToBatch(Long batchId, Long staffId) {
        ProductionBatch batch = getBatchById(batchId);
        
        // Verify staff exists
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        batch.setAssignedStaffId(staffId);
        batch.setAssignedStaff(staff);
        
        return productionBatchRepository.save(batch);
    }
    
    @Override
    @Transactional
    public ProductionBatch updateBatchEquipment(Long batchId, String equipment) {
        ProductionBatch batch = getBatchById(batchId);
        batch.setEquipmentUsed(equipment);
        return productionBatchRepository.save(batch);
    }
    
    @Override
    public Integer calculateTotalWasteForProduct(Long productId) {
        return productionBatchRepository.calculateTotalWasteForProduct(productId);
    }
}