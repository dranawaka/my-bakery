package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing promotion usages.
 */
@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    
    /**
     * Find all promotion usages for a specific promotion.
     *
     * @param promotionId the promotion ID
     * @return a list of promotion usages for the specified promotion
     */
    List<PromotionUsage> findByPromotionId(Long promotionId);
    
    /**
     * Find all promotion usages for a specific customer.
     *
     * @param customerId the customer ID
     * @return a list of promotion usages for the specified customer
     */
    List<PromotionUsage> findByCustomerId(Long customerId);
    
    /**
     * Find all promotion usages for a specific order.
     *
     * @param orderId the order ID
     * @return a list of promotion usages for the specified order
     */
    List<PromotionUsage> findByOrderId(Long orderId);
    
    /**
     * Find all promotion usages for a specific promotion and customer.
     *
     * @param promotionId the promotion ID
     * @param customerId the customer ID
     * @return a list of promotion usages for the specified promotion and customer
     */
    List<PromotionUsage> findByPromotionIdAndCustomerId(Long promotionId, Long customerId);
    
    /**
     * Count the number of times a customer has used a specific promotion.
     *
     * @param promotionId the promotion ID
     * @param customerId the customer ID
     * @return the number of times the customer has used the promotion
     */
    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.promotionId = :promotionId AND pu.customerId = :customerId")
    int countByPromotionIdAndCustomerId(@Param("promotionId") Long promotionId, @Param("customerId") Long customerId);
    
    /**
     * Find all promotion usages within a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of promotion usages within the specified date range
     */
    List<PromotionUsage> findByUsedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all promotion usages for a specific promotion within a date range.
     *
     * @param promotionId the promotion ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of promotion usages for the specified promotion within the specified date range
     */
    List<PromotionUsage> findByPromotionIdAndUsedAtBetween(Long promotionId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all promotion usages for a specific customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of promotion usages for the specified customer within the specified date range
     */
    List<PromotionUsage> findByCustomerIdAndUsedAtBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}