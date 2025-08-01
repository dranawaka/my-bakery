package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.LoyaltyPoints;
import com.aurelius.tech.mybakery.model.LoyaltyPoints.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing loyalty points transactions.
 */
@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, Long> {
    
    /**
     * Find all loyalty points transactions for a specific customer.
     *
     * @param customerId the customer ID
     * @return a list of loyalty points transactions
     */
    List<LoyaltyPoints> findByCustomerId(Long customerId);
    
    /**
     * Find all loyalty points transactions for a specific customer with a specific transaction type.
     *
     * @param customerId the customer ID
     * @param transactionType the transaction type
     * @return a list of loyalty points transactions
     */
    List<LoyaltyPoints> findByCustomerIdAndTransactionType(Long customerId, TransactionType transactionType);
    
    /**
     * Find all loyalty points transactions for a specific customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of loyalty points transactions
     */
    List<LoyaltyPoints> findByCustomerIdAndCreatedAtBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all loyalty points transactions that are expiring soon.
     *
     * @param expiryDate the expiry date
     * @return a list of loyalty points transactions
     */
    List<LoyaltyPoints> findByExpiryDateBefore(LocalDateTime expiryDate);
    
    /**
     * Calculate the total active points for a specific customer.
     *
     * @param customerId the customer ID
     * @return the total active points
     */
    @Query("SELECT COALESCE(SUM(lp.points), 0) FROM LoyaltyPoints lp WHERE lp.customerId = :customerId AND (lp.expiryDate IS NULL OR lp.expiryDate > CURRENT_TIMESTAMP)")
    Integer calculateTotalActivePoints(@Param("customerId") Long customerId);
    
    /**
     * Find the most recent loyalty points transaction for a specific customer.
     *
     * @param customerId the customer ID
     * @return the most recent loyalty points transaction
     */
    LoyaltyPoints findFirstByCustomerIdOrderByCreatedAtDesc(Long customerId);
}