package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Report;
import com.aurelius.tech.mybakery.model.Report.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Report entity.
 * Provides methods for CRUD operations on Report entities.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * Find reports by type.
     *
     * @param type the report type to search for
     * @return a list of reports with the given type
     */
    List<Report> findByType(ReportType type);
    
    /**
     * Find reports by created by user ID.
     *
     * @param createdBy the user ID who created the reports
     * @return a list of reports created by the given user
     */
    List<Report> findByCreatedBy(Long createdBy);
    
    /**
     * Find reports created between the given dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of reports created between the given dates
     */
    List<Report> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find reports by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @return a list of reports with names containing the given string
     */
    List<Report> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find reports by type and created by user ID.
     *
     * @param type the report type to search for
     * @param createdBy the user ID who created the reports
     * @return a list of reports with the given type and created by the given user
     */
    List<Report> findByTypeAndCreatedBy(ReportType type, Long createdBy);
    
    /**
     * Find reports by type and created between the given dates.
     *
     * @param type the report type to search for
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of reports with the given type and created between the given dates
     */
    List<Report> findByTypeAndCreatedAtBetween(ReportType type, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find reports by file type.
     *
     * @param fileType the file type to search for
     * @return a list of reports with the given file type
     */
    List<Report> findByFileType(String fileType);
}