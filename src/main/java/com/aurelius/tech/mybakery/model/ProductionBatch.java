package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a production batch.
 */
@Entity
@Table(name = "production_batches")
public class ProductionBatch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;
    
    @Column(name = "recipe_id")
    private Long recipeId;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private Recipe recipe;
    
    @Column(name = "product_id")
    private Long productId;
    
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "batch_size", nullable = false)
    private Integer batchSize;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus status;
    
    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;
    
    @ManyToOne
    @JoinColumn(name = "assigned_staff_id", insertable = false, updatable = false)
    private User assignedStaff;
    
    @Column(name = "equipment_used")
    private String equipmentUsed;
    
    @Column(name = "quality_check_passed")
    private Boolean qualityCheckPassed;
    
    @Column(name = "quality_notes")
    private String qualityNotes;
    
    @Column(name = "waste_quantity")
    private Integer wasteQuantity;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing the status of a production batch.
     */
    public enum BatchStatus {
        PLANNED,
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        QUALITY_CHECK,
        CANCELLED
    }
    
    /**
     * Pre-persist hook to set created and updated timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BatchStatus.PLANNED;
        }
        if (this.batchNumber == null || this.batchNumber.isEmpty()) {
            this.batchNumber = generateBatchNumber();
        }
    }
    
    /**
     * Pre-update hook to update the updated timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Generate a batch number.
     *
     * @return the generated batch number
     */
    private String generateBatchNumber() {
        // Format: YYYYMMDD-PRODUCTID-RANDOM
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String productStr = productId != null ? productId.toString() : "0000";
        String randomStr = String.format("%04d", (int) (Math.random() * 10000));
        
        return dateStr + "-" + productStr + "-" + randomStr;
    }
    
    /**
     * Start the production batch.
     */
    public void start() {
        if (this.status == BatchStatus.SCHEDULED) {
            this.status = BatchStatus.IN_PROGRESS;
            this.actualStartTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Batch must be in SCHEDULED status to start");
        }
    }
    
    /**
     * Complete the production batch.
     */
    public void complete() {
        if (this.status == BatchStatus.IN_PROGRESS) {
            this.status = BatchStatus.COMPLETED;
            this.actualEndTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Batch must be in IN_PROGRESS status to complete");
        }
    }
    
    /**
     * Cancel the production batch.
     */
    public void cancel() {
        if (this.status == BatchStatus.PLANNED || this.status == BatchStatus.SCHEDULED) {
            this.status = BatchStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Batch must be in PLANNED or SCHEDULED status to cancel");
        }
    }
    
    /**
     * Perform quality check on the production batch.
     *
     * @param passed whether the quality check passed
     * @param notes quality check notes
     * @param wasteQuantity quantity of waste
     */
    public void performQualityCheck(boolean passed, String notes, Integer wasteQuantity) {
        if (this.status == BatchStatus.COMPLETED) {
            this.status = BatchStatus.QUALITY_CHECK;
            this.qualityCheckPassed = passed;
            this.qualityNotes = notes;
            this.wasteQuantity = wasteQuantity;
        } else {
            throw new IllegalStateException("Batch must be in COMPLETED status to perform quality check");
        }
    }
    
    // Constructors
    public ProductionBatch() {
    }
    
    public ProductionBatch(Long recipeId, Long productId, Integer quantity, Integer batchSize,
                          LocalDateTime scheduledStartTime, LocalDateTime scheduledEndTime,
                          Long assignedStaffId) {
        this.recipeId = recipeId;
        this.productId = productId;
        this.quantity = quantity;
        this.batchSize = batchSize;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.assignedStaffId = assignedStaffId;
        this.status = BatchStatus.PLANNED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBatchNumber() {
        return batchNumber;
    }
    
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    public Long getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Integer getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
    
    public BatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(BatchStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }
    
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }
    
    public LocalDateTime getScheduledEndTime() {
        return scheduledEndTime;
    }
    
    public void setScheduledEndTime(LocalDateTime scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }
    
    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }
    
    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    
    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }
    
    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
    
    public Long getAssignedStaffId() {
        return assignedStaffId;
    }
    
    public void setAssignedStaffId(Long assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }
    
    public User getAssignedStaff() {
        return assignedStaff;
    }
    
    public void setAssignedStaff(User assignedStaff) {
        this.assignedStaff = assignedStaff;
    }
    
    public String getEquipmentUsed() {
        return equipmentUsed;
    }
    
    public void setEquipmentUsed(String equipmentUsed) {
        this.equipmentUsed = equipmentUsed;
    }
    
    public Boolean getQualityCheckPassed() {
        return qualityCheckPassed;
    }
    
    public void setQualityCheckPassed(Boolean qualityCheckPassed) {
        this.qualityCheckPassed = qualityCheckPassed;
    }
    
    public String getQualityNotes() {
        return qualityNotes;
    }
    
    public void setQualityNotes(String qualityNotes) {
        this.qualityNotes = qualityNotes;
    }
    
    public Integer getWasteQuantity() {
        return wasteQuantity;
    }
    
    public void setWasteQuantity(Integer wasteQuantity) {
        this.wasteQuantity = wasteQuantity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionBatch that = (ProductionBatch) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(batchNumber, that.batchNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, batchNumber);
    }
    
    @Override
    public String toString() {
        return "ProductionBatch{" +
               "id=" + id +
               ", batchNumber='" + batchNumber + '\'' +
               ", recipeId=" + recipeId +
               ", productId=" + productId +
               ", quantity=" + quantity +
               ", batchSize=" + batchSize +
               ", status=" + status +
               ", scheduledStartTime=" + scheduledStartTime +
               ", scheduledEndTime=" + scheduledEndTime +
               ", assignedStaffId=" + assignedStaffId +
               '}';
    }
}