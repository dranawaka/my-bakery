package com.aurelius.tech.mybakery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a staff schedule.
 */
@Entity
@Table(name = "staff_schedules")
public class StaffSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "staff_id", nullable = false)
    private Long staffId;
    
    @ManyToOne
    @JoinColumn(name = "staff_id", insertable = false, updatable = false)
    private User staff;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "schedule_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;
    
    @Column(name = "production_batch_id")
    private Long productionBatchId;
    
    @ManyToOne
    @JoinColumn(name = "production_batch_id", insertable = false, updatable = false)
    private ProductionBatch productionBatch;
    
    @Column(name = "station")
    private String station;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Enum representing the type of schedule.
     */
    public enum ScheduleType {
        REGULAR,
        OVERTIME,
        PRODUCTION,
        TRAINING,
        MEETING,
        LEAVE
    }
    
    /**
     * Enum representing the status of a schedule.
     */
    public enum ScheduleStatus {
        SCHEDULED,
        CONFIRMED,
        COMPLETED,
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
            this.status = ScheduleStatus.SCHEDULED;
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
     * Confirm the schedule.
     */
    public void confirm() {
        if (this.status == ScheduleStatus.SCHEDULED) {
            this.status = ScheduleStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Schedule must be in SCHEDULED status to confirm");
        }
    }
    
    /**
     * Complete the schedule.
     */
    public void complete() {
        if (this.status == ScheduleStatus.CONFIRMED) {
            this.status = ScheduleStatus.COMPLETED;
        } else {
            throw new IllegalStateException("Schedule must be in CONFIRMED status to complete");
        }
    }
    
    /**
     * Cancel the schedule.
     */
    public void cancel() {
        if (this.status == ScheduleStatus.SCHEDULED || this.status == ScheduleStatus.CONFIRMED) {
            this.status = ScheduleStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Schedule must be in SCHEDULED or CONFIRMED status to cancel");
        }
    }
    
    /**
     * Check if the schedule overlaps with another schedule.
     *
     * @param other the other schedule
     * @return true if the schedules overlap, false otherwise
     */
    public boolean overlaps(StaffSchedule other) {
        if (!this.staffId.equals(other.staffId)) {
            return false;
        }
        
        return (this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime));
    }
    
    // Constructors
    public StaffSchedule() {
    }
    
    public StaffSchedule(Long staffId, LocalDateTime startTime, LocalDateTime endTime,
                        ScheduleType scheduleType, ScheduleStatus status) {
        this.staffId = staffId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleType = scheduleType;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStaffId() {
        return staffId;
    }
    
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
    
    public User getStaff() {
        return staff;
    }
    
    public void setStaff(User staff) {
        this.staff = staff;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public ScheduleType getScheduleType() {
        return scheduleType;
    }
    
    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }
    
    public Long getProductionBatchId() {
        return productionBatchId;
    }
    
    public void setProductionBatchId(Long productionBatchId) {
        this.productionBatchId = productionBatchId;
    }
    
    public ProductionBatch getProductionBatch() {
        return productionBatch;
    }
    
    public void setProductionBatch(ProductionBatch productionBatch) {
        this.productionBatch = productionBatch;
    }
    
    public String getStation() {
        return station;
    }
    
    public void setStation(String station) {
        this.station = station;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public ScheduleStatus getStatus() {
        return status;
    }
    
    public void setStatus(ScheduleStatus status) {
        this.status = status;
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
        StaffSchedule that = (StaffSchedule) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(staffId, that.staffId) &&
               Objects.equals(startTime, that.startTime) &&
               Objects.equals(endTime, that.endTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, staffId, startTime, endTime);
    }
    
    @Override
    public String toString() {
        return "StaffSchedule{" +
               "id=" + id +
               ", staffId=" + staffId +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", scheduleType=" + scheduleType +
               ", productionBatchId=" + productionBatchId +
               ", status=" + status +
               '}';
    }
}