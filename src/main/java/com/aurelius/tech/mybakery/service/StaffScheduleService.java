package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.StaffSchedule;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleStatus;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing staff schedules.
 */
public interface StaffScheduleService {
    
    /**
     * Get all staff schedules.
     *
     * @return all staff schedules
     */
    List<StaffSchedule> getAllSchedules();
    
    /**
     * Get a staff schedule by ID.
     *
     * @param id the schedule ID
     * @return the staff schedule with the specified ID
     */
    StaffSchedule getScheduleById(Long id);
    
    /**
     * Get all schedules for a specific staff member.
     *
     * @param staffId the staff ID
     * @return all schedules for the specified staff member
     */
    List<StaffSchedule> getSchedulesByStaffId(Long staffId);
    
    /**
     * Get all schedules for a specific staff member with a specific status.
     *
     * @param staffId the staff ID
     * @param status the schedule status
     * @return all schedules for the specified staff member with the specified status
     */
    List<StaffSchedule> getSchedulesByStaffIdAndStatus(Long staffId, ScheduleStatus status);
    
    /**
     * Get all schedules for a specific staff member with a specific type.
     *
     * @param staffId the staff ID
     * @param scheduleType the schedule type
     * @return all schedules for the specified staff member with the specified type
     */
    List<StaffSchedule> getSchedulesByStaffIdAndType(Long staffId, ScheduleType scheduleType);
    
    /**
     * Get all schedules for a specific production batch.
     *
     * @param productionBatchId the production batch ID
     * @return all schedules for the specified production batch
     */
    List<StaffSchedule> getSchedulesByProductionBatchId(Long productionBatchId);
    
    /**
     * Get all schedules with a specific status.
     *
     * @param status the schedule status
     * @return all schedules with the specified status
     */
    List<StaffSchedule> getSchedulesByStatus(ScheduleStatus status);
    
    /**
     * Get all schedules with a specific type.
     *
     * @param scheduleType the schedule type
     * @return all schedules with the specified type
     */
    List<StaffSchedule> getSchedulesByType(ScheduleType scheduleType);
    
    /**
     * Get all schedules for a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return all schedules for the specified date range
     */
    List<StaffSchedule> getSchedulesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all schedules for a specific staff member for a specific date range.
     *
     * @param staffId the staff ID
     * @param startDate the start date
     * @param endDate the end date
     * @return all schedules for the specified staff member for the specified date range
     */
    List<StaffSchedule> getSchedulesByStaffIdAndDateRange(Long staffId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all schedules for a specific station.
     *
     * @param station the station
     * @return all schedules for the specified station
     */
    List<StaffSchedule> getSchedulesByStation(String station);
    
    /**
     * Get all schedules for today.
     *
     * @return all schedules for today
     */
    List<StaffSchedule> getSchedulesForToday();
    
    /**
     * Get all schedules for a specific staff member for today.
     *
     * @param staffId the staff ID
     * @return all schedules for the specified staff member for today
     */
    List<StaffSchedule> getSchedulesForStaffToday(Long staffId);
    
    /**
     * Find all schedules that overlap with a specific time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return all schedules that overlap with the specified time range
     */
    List<StaffSchedule> findOverlappingSchedules(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find all schedules for a specific staff member that overlap with a specific time range.
     *
     * @param staffId the staff ID
     * @param startTime the start time
     * @param endTime the end time
     * @return all schedules for the specified staff member that overlap with the specified time range
     */
    List<StaffSchedule> findOverlappingSchedulesForStaff(Long staffId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Create a new staff schedule.
     *
     * @param schedule the staff schedule to create
     * @return the created staff schedule
     */
    StaffSchedule createSchedule(StaffSchedule schedule);
    
    /**
     * Update an existing staff schedule.
     *
     * @param id the schedule ID
     * @param schedule the updated staff schedule
     * @return the updated staff schedule
     */
    StaffSchedule updateSchedule(Long id, StaffSchedule schedule);
    
    /**
     * Delete a staff schedule.
     *
     * @param id the schedule ID
     */
    void deleteSchedule(Long id);
    
    /**
     * Confirm a staff schedule.
     *
     * @param id the schedule ID
     * @return the confirmed staff schedule
     */
    StaffSchedule confirmSchedule(Long id);
    
    /**
     * Complete a staff schedule.
     *
     * @param id the schedule ID
     * @return the completed staff schedule
     */
    StaffSchedule completeSchedule(Long id);
    
    /**
     * Cancel a staff schedule.
     *
     * @param id the schedule ID
     * @return the cancelled staff schedule
     */
    StaffSchedule cancelSchedule(Long id);
    
    /**
     * Check if a new schedule would overlap with existing schedules for a staff member.
     *
     * @param staffId the staff ID
     * @param startTime the start time
     * @param endTime the end time
     * @param excludeScheduleId the ID of a schedule to exclude from the check (for updates)
     * @return true if there would be an overlap, false otherwise
     */
    boolean wouldOverlap(Long staffId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId);
    
    /**
     * Count the number of schedules for a specific staff member within a specific date range.
     *
     * @param staffId the staff ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of schedules for the specified staff member within the specified date range
     */
    int countSchedulesForStaffInDateRange(Long staffId, LocalDateTime startDate, LocalDateTime endDate);
}