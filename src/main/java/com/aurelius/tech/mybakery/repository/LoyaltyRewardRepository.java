package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.LoyaltyReward;
import com.aurelius.tech.mybakery.model.LoyaltyReward.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing loyalty rewards.
 */
@Repository
public interface LoyaltyRewardRepository extends JpaRepository<LoyaltyReward, Long> {
    
    /**
     * Find all active loyalty rewards.
     *
     * @return a list of active loyalty rewards
     */
    List<LoyaltyReward> findByActiveTrue();
    
    /**
     * Find all active loyalty rewards of a specific type.
     *
     * @param rewardType the reward type
     * @return a list of active loyalty rewards of the specified type
     */
    List<LoyaltyReward> findByActiveTrueAndRewardType(RewardType rewardType);
    
    /**
     * Find all active loyalty rewards with a points cost less than or equal to the specified value.
     *
     * @param pointsCost the maximum points cost
     * @return a list of active loyalty rewards with a points cost less than or equal to the specified value
     */
    List<LoyaltyReward> findByActiveTrueAndPointsCostLessThanEqual(Integer pointsCost);
    
    /**
     * Find all active loyalty rewards that are available within a specific date range.
     *
     * @param currentDate the current date
     * @return a list of active loyalty rewards that are available within the specified date range
     */
    List<LoyaltyReward> findByActiveTrueAndStartDateBeforeAndEndDateAfterOrEndDateIsNull(
            LocalDateTime currentDate, LocalDateTime currentDate2);
    
    /**
     * Find all active loyalty rewards for a specific product.
     *
     * @param productId the product ID
     * @return a list of active loyalty rewards for the specified product
     */
    List<LoyaltyReward> findByActiveTrueAndProductId(Long productId);
}