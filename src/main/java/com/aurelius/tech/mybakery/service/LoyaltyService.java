package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.LoyaltyPoints;
import com.aurelius.tech.mybakery.model.LoyaltyReward;
import com.aurelius.tech.mybakery.model.LoyaltyTier;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing the loyalty program.
 */
public interface LoyaltyService {
    
    /**
     * Get the total active points for a customer.
     *
     * @param customerId the customer ID
     * @return the total active points
     */
    Integer getCustomerPoints(Long customerId);
    
    /**
     * Get the loyalty tier for a customer based on their points.
     *
     * @param customerId the customer ID
     * @return the customer's loyalty tier
     */
    LoyaltyTier getCustomerTier(Long customerId);
    
    /**
     * Get the next loyalty tier for a customer.
     *
     * @param customerId the customer ID
     * @return the next loyalty tier for the customer
     */
    LoyaltyTier getNextTier(Long customerId);
    
    /**
     * Get the points needed to reach the next tier.
     *
     * @param customerId the customer ID
     * @return the points needed to reach the next tier
     */
    Integer getPointsToNextTier(Long customerId);
    
    /**
     * Award points to a customer for a purchase.
     *
     * @param customerId the customer ID
     * @param orderId the order ID
     * @param amount the purchase amount
     * @return the loyalty points transaction
     */
    LoyaltyPoints awardPointsForPurchase(Long customerId, Long orderId, Double amount);
    
    /**
     * Award bonus points to a customer.
     *
     * @param customerId the customer ID
     * @param points the points to award
     * @param description the description of the bonus
     * @return the loyalty points transaction
     */
    LoyaltyPoints awardBonusPoints(Long customerId, Integer points, String description);
    
    /**
     * Redeem points for a reward.
     *
     * @param customerId the customer ID
     * @param rewardId the reward ID
     * @return the loyalty points transaction
     */
    LoyaltyPoints redeemPoints(Long customerId, Long rewardId);
    
    /**
     * Get the loyalty points history for a customer.
     *
     * @param customerId the customer ID
     * @return the loyalty points history
     */
    List<LoyaltyPoints> getPointsHistory(Long customerId);
    
    /**
     * Get the loyalty points history for a customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the loyalty points history
     */
    List<LoyaltyPoints> getPointsHistory(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get all available rewards for a customer based on their points.
     *
     * @param customerId the customer ID
     * @return the available rewards
     */
    List<LoyaltyReward> getAvailableRewards(Long customerId);
    
    /**
     * Get all loyalty tiers.
     *
     * @return all loyalty tiers
     */
    List<LoyaltyTier> getAllTiers();
    
    /**
     * Create a new loyalty reward.
     *
     * @param reward the loyalty reward to create
     * @return the created loyalty reward
     */
    LoyaltyReward createReward(LoyaltyReward reward);
    
    /**
     * Update a loyalty reward.
     *
     * @param id the reward ID
     * @param reward the updated loyalty reward
     * @return the updated loyalty reward
     */
    LoyaltyReward updateReward(Long id, LoyaltyReward reward);
    
    /**
     * Delete a loyalty reward.
     *
     * @param id the reward ID
     */
    void deleteReward(Long id);
    
    /**
     * Create a new loyalty tier.
     *
     * @param tier the loyalty tier to create
     * @return the created loyalty tier
     */
    LoyaltyTier createTier(LoyaltyTier tier);
    
    /**
     * Update a loyalty tier.
     *
     * @param id the tier ID
     * @param tier the updated loyalty tier
     * @return the updated loyalty tier
     */
    LoyaltyTier updateTier(Long id, LoyaltyTier tier);
    
    /**
     * Delete a loyalty tier.
     *
     * @param id the tier ID
     */
    void deleteTier(Long id);
    
    /**
     * Process expired points.
     *
     * @return the number of expired points transactions processed
     */
    int processExpiredPoints();
}