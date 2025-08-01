package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.StaffSchedule;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleStatus;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing staff schedules.
 */
@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    
    /**
     * Find all schedules for a specific staff member.
     *
     * @param staffId the staff ID
     * @return a list of schedules for the specified staff member
     */
    List<StaffSchedule> findByStaffId(Long staffId);
    
    /**
     * Find all schedules for a specific staff member with a specific status.
     *
     * @param staffId the staff ID
     * @param status the schedule status
     * @return a list of schedules for the specified staff member with the specified status
     */
    List<StaffSchedule> findByStaffIdAndStatus(Long staffId, ScheduleStatus status);
    
    /**
     * Find all schedules for a specific staff member with a specific type.
     *
     * @param staffId the staff ID
     * @param scheduleType the schedule type
     * @return a list of schedules for the specified staff member with the specified type
     */
    List<StaffSchedule> findByStaffIdAndScheduleType(Long staffId, ScheduleType scheduleType);
    
    /**
     * Find all schedules for a specific production batch.
     *
     * @param productionBatchId the production batch ID
     * @return a list of schedules for the specified production batch
     */
    List<StaffSchedule> findByProductionBatchId(Long productionBatchId);
    
    /**
     * Find all schedules with a specific status.
     *
     * @param status the schedule status
     * @return a list of schedules with the specified status
     */
    List<StaffSchedule> findByStatus(ScheduleStatus status);
    
    /**
     * Find all schedules with a specific type.
     *
     * @param scheduleType the schedule type
     * @return a list of schedules with the specified type
     */
    List<StaffSchedule> findByScheduleType(ScheduleType scheduleType);
    
    /**
     * Find all schedules that start within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of schedules that start within the specified date range
     */
    List<StaffSchedule> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all schedules that end within a specific date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of schedules that end within the specified date range
     */
    List<StaffSchedule> findByEndTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all schedules for a specific staff member that start within a specific date range.
     *
     * @param staffId the staff ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of schedules for the specified staff member that start within the specified date range
     */
    List<StaffSchedule> findByStaffIdAndStartTimeBetween(Long staffId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all schedules for a specific staff member that end within a specific date range.
     *
     * @param staffId the staff ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of schedules for the specified staff member that end within the specified date range
     */
    List<StaffSchedule> findByStaffIdAndEndTimeBetween(Long staffId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all schedules for a specific station.
     *
     * @param station the station
     * @return a list of schedules for the specified station
     */
    List<StaffSchedule> findByStation(String station);
    
    /**
     * Find all schedules for today.
     *
     * @param startOfDay the start of the day
     * @param endOfDay the end of the day
     * @return a list of schedules for today
     */
    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.startTime BETWEEN :startOfDay AND :endOfDay")
    List<StaffSchedule> findSchedulesForToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Find all schedules for a specific staff member for today.
     *
     * @param staffId the staff ID
     * @param startOfDay the start of the day
     * @param endOfDay the end of the day
     * @return a list of schedules for the specified staff member for today
     */
    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.staffId = :staffId AND ss.startTime BETWEEN :startOfDay AND :endOfDay")
    List<StaffSchedule> findSchedulesForStaffToday(@Param("staffId") Long staffId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Find all schedules that overlap with a specific time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return a list of schedules that overlap with the specified time range
     */
    @Query("SELECT ss FROM StaffSchedule ss WHERE (ss.startTime <= :endTime AND ss.endTime >= :startTime)")
    List<StaffSchedule> findOverlappingSchedules(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find all schedules for a specific staff member that overlap with a specific time range.
     *
     * @param staffId the staff ID
     * @param startTime the start time
     * @param endTime the end time
     * @return a list of schedules for the specified staff member that overlap with the specified time range
     */
    @Query("SELECT ss FROM StaffSchedule ss WHERE ss.staffId = :staffId AND (ss.startTime <= :endTime AND ss.endTime >= :startTime)")
    List<StaffSchedule> findOverlappingSchedulesForStaff(@Param("staffId") Long staffId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * Count the number of schedules for a specific staff member within a specific date range.
     *
     * @param staffId the staff ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of schedules for the specified staff member within the specified date range
     */
    @Query("SELECT COUNT(ss) FROM StaffSchedule ss WHERE ss.staffId = :staffId AND ss.startTime BETWEEN :startDate AND :endDate")
    int countSchedulesForStaffInDateRange(@Param("staffId") Long staffId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}