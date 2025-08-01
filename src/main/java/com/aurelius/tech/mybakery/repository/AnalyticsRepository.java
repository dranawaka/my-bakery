package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Analytics;
import com.aurelius.tech.mybakery.model.Analytics.AnalyticsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Analytics entity.
 * Provides methods for CRUD operations on Analytics entities.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    
    /**
     * Find analytics by type.
     *
     * @param type the analytics type to search for
     * @return a list of analytics with the given type
     */
    List<Analytics> findByType(AnalyticsType type);
    
    /**
     * Find analytics by time period.
     *
     * @param timePeriod the time period to search for
     * @return a list of analytics with the given time period
     */
    List<Analytics> findByTimePeriod(String timePeriod);
    
    /**
     * Find analytics created between the given dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of analytics created between the given dates
     */
    List<Analytics> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find analytics by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @return a list of analytics with names containing the given string
     */
    List<Analytics> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find analytics by type and time period.
     *
     * @param type the analytics type to search for
     * @param timePeriod the time period to search for
     * @return a list of analytics with the given type and time period
     */
    List<Analytics> findByTypeAndTimePeriod(AnalyticsType type, String timePeriod);
    
    /**
     * Find analytics by type and created between the given dates.
     *
     * @param type the analytics type to search for
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of analytics with the given type and created between the given dates
     */
    List<Analytics> findByTypeAndCreatedAtBetween(AnalyticsType type, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find analytics with data for a specific time range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of analytics with data for the given time range
     */
    List<Analytics> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find the most recent analytics by type.
     *
     * @param type the analytics type to search for
     * @return a list of the most recent analytics with the given type
     */
    List<Analytics> findTop5ByTypeOrderByCreatedAtDesc(AnalyticsType type);
}