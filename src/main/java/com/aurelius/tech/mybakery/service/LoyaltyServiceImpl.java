package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.exception.ResourceNotFoundException;
import com.aurelius.tech.mybakery.model.LoyaltyPoints;
import com.aurelius.tech.mybakery.model.LoyaltyPoints.TransactionType;
import com.aurelius.tech.mybakery.model.LoyaltyReward;
import com.aurelius.tech.mybakery.model.LoyaltyTier;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.LoyaltyPointsRepository;
import com.aurelius.tech.mybakery.repository.LoyaltyRewardRepository;
import com.aurelius.tech.mybakery.repository.LoyaltyTierRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the LoyaltyService interface.
 */
@Service
public class LoyaltyServiceImpl implements LoyaltyService {
    
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final LoyaltyRewardRepository loyaltyRewardRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public LoyaltyServiceImpl(
            LoyaltyPointsRepository loyaltyPointsRepository,
            LoyaltyRewardRepository loyaltyRewardRepository,
            LoyaltyTierRepository loyaltyTierRepository,
            UserRepository userRepository) {
        this.loyaltyPointsRepository = loyaltyPointsRepository;
        this.loyaltyRewardRepository = loyaltyRewardRepository;
        this.loyaltyTierRepository = loyaltyTierRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Integer getCustomerPoints(Long customerId) {
        return loyaltyPointsRepository.calculateTotalActivePoints(customerId);
    }
    
    @Override
    public LoyaltyTier getCustomerTier(Long customerId) {
        Integer points = getCustomerPoints(customerId);
        List<LoyaltyTier> tiers = loyaltyTierRepository.findTierByPoints(points);
        return tiers.isEmpty() ? null : tiers.get(0);
    }
    
    @Override
    public LoyaltyTier getNextTier(Long customerId) {
        Integer points = getCustomerPoints(customerId);
        List<LoyaltyTier> nextTiers = loyaltyTierRepository.findNextTierByPoints(points);
        return nextTiers.isEmpty() ? null : nextTiers.get(0);
    }
    
    @Override
    public Integer getPointsToNextTier(Long customerId) {
        Integer points = getCustomerPoints(customerId);
        LoyaltyTier nextTier = getNextTier(customerId);
        return nextTier != null ? nextTier.getPointsThreshold() - points : 0;
    }
    
    @Override
    @Transactional
    public LoyaltyPoints awardPointsForPurchase(Long customerId, Long orderId, Double amount) {
        // Validate customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // Calculate points based on amount (1 point per $1 spent)
        int pointsToAward = (int) Math.floor(amount);
        
        // Apply tier multiplier if applicable
        LoyaltyTier tier = getCustomerTier(customerId);
        if (tier != null && tier.getPointsMultiplier() != null) {
            pointsToAward = (int) Math.floor(pointsToAward * tier.getPointsMultiplier());
        }
        
        // Create points transaction
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
        loyaltyPoints.setCustomerId(customerId);
        loyaltyPoints.setPoints(pointsToAward);
        loyaltyPoints.setTotalPoints(getCustomerPoints(customerId) + pointsToAward);
        loyaltyPoints.setTransactionType(TransactionType.EARN);
        loyaltyPoints.setTransactionReference("ORDER_" + orderId);
        loyaltyPoints.setDescription("Points earned for order #" + orderId);
        
        // Set expiry date (1 year from now)
        loyaltyPoints.setExpiryDate(LocalDateTime.now().plusYears(1));
        
        return loyaltyPointsRepository.save(loyaltyPoints);
    }
    
    @Override
    @Transactional
    public LoyaltyPoints awardBonusPoints(Long customerId, Integer points, String description) {
        // Validate customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // Create points transaction
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
        loyaltyPoints.setCustomerId(customerId);
        loyaltyPoints.setPoints(points);
        loyaltyPoints.setTotalPoints(getCustomerPoints(customerId) + points);
        loyaltyPoints.setTransactionType(TransactionType.EARN);
        loyaltyPoints.setTransactionReference("BONUS");
        loyaltyPoints.setDescription(description);
        
        // Set expiry date (1 year from now)
        loyaltyPoints.setExpiryDate(LocalDateTime.now().plusYears(1));
        
        return loyaltyPointsRepository.save(loyaltyPoints);
    }
    
    @Override
    @Transactional
    public LoyaltyPoints redeemPoints(Long customerId, Long rewardId) {
        // Validate customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // Validate reward
        LoyaltyReward reward = loyaltyRewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));
        
        // Check if reward is active
        if (!reward.getActive()) {
            throw new IllegalStateException("Reward is not active");
        }
        
        // Check if customer has enough points
        Integer customerPoints = getCustomerPoints(customerId);
        if (customerPoints < reward.getPointsCost()) {
            throw new IllegalStateException("Not enough points to redeem this reward");
        }
        
        // Create points transaction
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
        loyaltyPoints.setCustomerId(customerId);
        loyaltyPoints.setPoints(-reward.getPointsCost()); // Negative points for redemption
        loyaltyPoints.setTotalPoints(customerPoints - reward.getPointsCost());
        loyaltyPoints.setTransactionType(TransactionType.REDEEM);
        loyaltyPoints.setTransactionReference("REWARD_" + rewardId);
        loyaltyPoints.setDescription("Points redeemed for " + reward.getName());
        
        return loyaltyPointsRepository.save(loyaltyPoints);
    }
    
    @Override
    public List<LoyaltyPoints> getPointsHistory(Long customerId) {
        return loyaltyPointsRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<LoyaltyPoints> getPointsHistory(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return loyaltyPointsRepository.findByCustomerIdAndCreatedAtBetween(customerId, startDate, endDate);
    }
    
    @Override
    public List<LoyaltyReward> getAvailableRewards(Long customerId) {
        Integer points = getCustomerPoints(customerId);
        return loyaltyRewardRepository.findByActiveTrueAndPointsCostLessThanEqual(points);
    }
    
    @Override
    public List<LoyaltyTier> getAllTiers() {
        return loyaltyTierRepository.findByActiveTrueOrderByPointsThresholdAsc();
    }
    
    @Override
    @Transactional
    public LoyaltyReward createReward(LoyaltyReward reward) {
        return loyaltyRewardRepository.save(reward);
    }
    
    @Override
    @Transactional
    public LoyaltyReward updateReward(Long id, LoyaltyReward reward) {
        LoyaltyReward existingReward = loyaltyRewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));
        
        existingReward.setName(reward.getName());
        existingReward.setDescription(reward.getDescription());
        existingReward.setPointsCost(reward.getPointsCost());
        existingReward.setDiscountAmount(reward.getDiscountAmount());
        existingReward.setDiscountPercentage(reward.getDiscountPercentage());
        existingReward.setProductId(reward.getProductId());
        existingReward.setActive(reward.getActive());
        existingReward.setRewardType(reward.getRewardType());
        existingReward.setStartDate(reward.getStartDate());
        existingReward.setEndDate(reward.getEndDate());
        
        return loyaltyRewardRepository.save(existingReward);
    }
    
    @Override
    @Transactional
    public void deleteReward(Long id) {
        LoyaltyReward reward = loyaltyRewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));
        
        loyaltyRewardRepository.delete(reward);
    }
    
    @Override
    @Transactional
    public LoyaltyTier createTier(LoyaltyTier tier) {
        return loyaltyTierRepository.save(tier);
    }
    
    @Override
    @Transactional
    public LoyaltyTier updateTier(Long id, LoyaltyTier tier) {
        LoyaltyTier existingTier = loyaltyTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
        
        existingTier.setName(tier.getName());
        existingTier.setDescription(tier.getDescription());
        existingTier.setPointsThreshold(tier.getPointsThreshold());
        existingTier.setPointsMultiplier(tier.getPointsMultiplier());
        existingTier.setDiscountPercentage(tier.getDiscountPercentage());
        existingTier.setFreeShipping(tier.getFreeShipping());
        existingTier.setBirthdayBonus(tier.getBirthdayBonus());
        existingTier.setExclusiveOffers(tier.getExclusiveOffers());
        existingTier.setPrioritySupport(tier.getPrioritySupport());
        existingTier.setEarlyAccess(tier.getEarlyAccess());
        existingTier.setActive(tier.getActive());
        
        return loyaltyTierRepository.save(existingTier);
    }
    
    @Override
    @Transactional
    public void deleteTier(Long id) {
        LoyaltyTier tier = loyaltyTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
        
        loyaltyTierRepository.delete(tier);
    }
    
    @Override
    @Transactional
    public int processExpiredPoints() {
        List<LoyaltyPoints> expiringPoints = loyaltyPointsRepository.findByExpiryDateBefore(LocalDateTime.now());
        
        for (LoyaltyPoints points : expiringPoints) {
            // Create expiry transaction
            LoyaltyPoints expiryTransaction = new LoyaltyPoints();
            expiryTransaction.setCustomerId(points.getCustomerId());
            expiryTransaction.setPoints(-points.getPoints()); // Negative points for expiry
            expiryTransaction.setTotalPoints(getCustomerPoints(points.getCustomerId()) - points.getPoints());
            expiryTransaction.setTransactionType(TransactionType.EXPIRE);
            expiryTransaction.setTransactionReference("EXPIRY_" + points.getId());
            expiryTransaction.setDescription("Points expired from transaction #" + points.getId());
            
            loyaltyPointsRepository.save(expiryTransaction);
        }
        
        return expiringPoints.size();
    }
}