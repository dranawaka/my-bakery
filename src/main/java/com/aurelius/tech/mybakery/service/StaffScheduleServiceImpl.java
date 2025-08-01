package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.exception.ResourceNotFoundException;
import com.aurelius.tech.mybakery.model.ProductionBatch;
import com.aurelius.tech.mybakery.model.StaffSchedule;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleStatus;
import com.aurelius.tech.mybakery.model.StaffSchedule.ScheduleType;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.ProductionBatchRepository;
import com.aurelius.tech.mybakery.repository.StaffScheduleRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the StaffScheduleService interface.
 */
@Service
public class StaffScheduleServiceImpl implements StaffScheduleService {
    
    private final StaffScheduleRepository staffScheduleRepository;
    private final UserRepository userRepository;
    private final ProductionBatchRepository productionBatchRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public StaffScheduleServiceImpl(
            StaffScheduleRepository staffScheduleRepository,
            UserRepository userRepository,
            ProductionBatchRepository productionBatchRepository) {
        this.staffScheduleRepository = staffScheduleRepository;
        this.userRepository = userRepository;
        this.productionBatchRepository = productionBatchRepository;
    }
    
    @Override
    public List<StaffSchedule> getAllSchedules() {
        return staffScheduleRepository.findAll();
    }
    
    @Override
    public StaffSchedule getScheduleById(Long id) {
        return staffScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff schedule not found with id: " + id));
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStaffId(Long staffId) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return staffScheduleRepository.findByStaffId(staffId);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStaffIdAndStatus(Long staffId, ScheduleStatus status) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return staffScheduleRepository.findByStaffIdAndStatus(staffId, status);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStaffIdAndType(Long staffId, ScheduleType scheduleType) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return staffScheduleRepository.findByStaffIdAndScheduleType(staffId, scheduleType);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByProductionBatchId(Long productionBatchId) {
        // Verify production batch exists
        productionBatchRepository.findById(productionBatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Production batch not found with id: " + productionBatchId));
        
        return staffScheduleRepository.findByProductionBatchId(productionBatchId);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStatus(ScheduleStatus status) {
        return staffScheduleRepository.findByStatus(status);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByType(ScheduleType scheduleType) {
        return staffScheduleRepository.findByScheduleType(scheduleType);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return staffScheduleRepository.findByStartTimeBetween(startDate, endDate);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStaffIdAndDateRange(Long staffId, LocalDateTime startDate, LocalDateTime endDate) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return staffScheduleRepository.findByStaffIdAndStartTimeBetween(staffId, startDate, endDate);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesByStation(String station) {
        return staffScheduleRepository.findByStation(station);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesForToday() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        return staffScheduleRepository.findSchedulesForToday(startOfDay, endOfDay);
    }
    
    @Override
    public List<StaffSchedule> getSchedulesForStaffToday(Long staffId) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        return staffScheduleRepository.findSchedulesForStaffToday(staffId, startOfDay, endOfDay);
    }
    
    @Override
    public List<StaffSchedule> findOverlappingSchedules(LocalDateTime startTime, LocalDateTime endTime) {
        return staffScheduleRepository.findOverlappingSchedules(startTime, endTime);
    }
    
    @Override
    public List<StaffSchedule> findOverlappingSchedulesForStaff(Long staffId, LocalDateTime startTime, LocalDateTime endTime) {
        // Verify staff exists
        userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
        
        return staffScheduleRepository.findOverlappingSchedulesForStaff(staffId, startTime, endTime);
    }
    
    @Override
    @Transactional
    public StaffSchedule createSchedule(StaffSchedule schedule) {
        // Validate staff
        User staff = userRepository.findById(schedule.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + schedule.getStaffId()));
        
        // Validate production batch if provided
        if (schedule.getProductionBatchId() != null) {
            ProductionBatch batch = productionBatchRepository.findById(schedule.getProductionBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Production batch not found with id: " + schedule.getProductionBatchId()));
            
            schedule.setProductionBatch(batch);
        }
        
        // Check for overlapping schedules
        if (wouldOverlap(schedule.getStaffId(), schedule.getStartTime(), schedule.getEndTime(), null)) {
            throw new IllegalStateException("Schedule overlaps with existing schedules for staff member");
        }
        
        // Set initial status if not provided
        if (schedule.getStatus() == null) {
            schedule.setStatus(ScheduleStatus.SCHEDULED);
        }
        
        return staffScheduleRepository.save(schedule);
    }
    
    @Override
    @Transactional
    public StaffSchedule updateSchedule(Long id, StaffSchedule schedule) {
        StaffSchedule existingSchedule = getScheduleById(id);
        
        // Validate staff if changed
        if (!schedule.getStaffId().equals(existingSchedule.getStaffId())) {
            userRepository.findById(schedule.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + schedule.getStaffId()));
        }
        
        // Validate production batch if changed
        if (schedule.getProductionBatchId() != null && 
            (existingSchedule.getProductionBatchId() == null || 
             !schedule.getProductionBatchId().equals(existingSchedule.getProductionBatchId()))) {
            
            productionBatchRepository.findById(schedule.getProductionBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Production batch not found with id: " + schedule.getProductionBatchId()));
        }
        
        // Check for overlapping schedules if time changed
        if (!schedule.getStartTime().equals(existingSchedule.getStartTime()) || 
            !schedule.getEndTime().equals(existingSchedule.getEndTime())) {
            
            if (wouldOverlap(schedule.getStaffId(), schedule.getStartTime(), schedule.getEndTime(), id)) {
                throw new IllegalStateException("Updated schedule would overlap with existing schedules for staff member");
            }
        }
        
        // Update fields
        existingSchedule.setStaffId(schedule.getStaffId());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        existingSchedule.setScheduleType(schedule.getScheduleType());
        existingSchedule.setProductionBatchId(schedule.getProductionBatchId());
        existingSchedule.setStation(schedule.getStation());
        existingSchedule.setNotes(schedule.getNotes());
        existingSchedule.setStatus(schedule.getStatus());
        
        return staffScheduleRepository.save(existingSchedule);
    }
    
    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        StaffSchedule schedule = getScheduleById(id);
        staffScheduleRepository.delete(schedule);
    }
    
    @Override
    @Transactional
    public StaffSchedule confirmSchedule(Long id) {
        StaffSchedule schedule = getScheduleById(id);
        
        try {
            schedule.confirm();
            return staffScheduleRepository.save(schedule);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot confirm schedule: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public StaffSchedule completeSchedule(Long id) {
        StaffSchedule schedule = getScheduleById(id);
        
        try {
            schedule.complete();
            return staffScheduleRepository.save(schedule);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot complete schedule: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public StaffSchedule cancelSchedule(Long id) {
        StaffSchedule schedule = getScheduleById(id);
        
        try {
            schedule.cancel();
            return staffScheduleRepository.save(schedule);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot cancel schedule: " + e.getMessage());
        }
    }
    
    @Override
    public boolean wouldOverlap(Long staffId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId) {
        List<StaffSchedule> overlappingSchedules = staffScheduleRepository.findOverlappingSchedulesForStaff(
                staffId, startTime, endTime);
        
        // Filter out the schedule being updated (if applicable)
        if (excludeScheduleId != null) {
            overlappingSchedules = overlappingSchedules.stream()
                    .filter(s -> !s.getId().equals(excludeScheduleId))
                    .collect(Collectors.toList());
        }
        
        return !overlappingSchedules.isEmpty();
    }
    
    @Override
    public int countSchedulesForStaffInDateRange(Long staffId, LocalDateTime startDate, LocalDateTime endDate) {
        return staffScheduleRepository.countSchedulesForStaffInDateRange(staffId, startDate, endDate);
    }
}