package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing loyalty tiers.
 */
@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {
    
    /**
     * Find all active loyalty tiers.
     *
     * @return a list of active loyalty tiers
     */
    List<LoyaltyTier> findByActiveTrue();
    
    /**
     * Find all active loyalty tiers ordered by points threshold.
     *
     * @return a list of active loyalty tiers ordered by points threshold
     */
    List<LoyaltyTier> findByActiveTrueOrderByPointsThresholdAsc();
    
    /**
     * Find the loyalty tier for a specific points total.
     *
     * @param points the points total
     * @return the loyalty tier for the specified points total
     */
    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.active = true AND lt.pointsThreshold <= :points ORDER BY lt.pointsThreshold DESC")
    List<LoyaltyTier> findTierByPoints(@Param("points") Integer points);
    
    /**
     * Find the next loyalty tier for a specific points total.
     *
     * @param points the points total
     * @return the next loyalty tier for the specified points total
     */
    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.active = true AND lt.pointsThreshold > :points ORDER BY lt.pointsThreshold ASC")
    List<LoyaltyTier> findNextTierByPoints(@Param("points") Integer points);
    
    /**
     * Find a loyalty tier by name.
     *
     * @param name the tier name
     * @return the loyalty tier with the specified name
     */
    Optional<LoyaltyTier> findByName(String name);
}