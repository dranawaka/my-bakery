package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.LoyaltyPoints;
import com.aurelius.tech.mybakery.model.LoyaltyReward;
import com.aurelius.tech.mybakery.model.LoyaltyTier;
import com.aurelius.tech.mybakery.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling loyalty program endpoints.
 */
@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {
    
    private final LoyaltyService loyaltyService;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }
    
    /**
     * Get the total points for a customer.
     *
     * @param customerId the customer ID
     * @return a response entity with the customer's points
     */
    @GetMapping("/points/{customerId}")
    public ResponseEntity<ApiResponse<?>> getCustomerPoints(@PathVariable Long customerId) {
        try {
            Integer points = loyaltyService.getCustomerPoints(customerId);
            return ResponseEntity.ok(ApiResponse.success(points, "Customer points retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the loyalty tier for a customer.
     *
     * @param customerId the customer ID
     * @return a response entity with the customer's tier
     */
    @GetMapping("/tier/{customerId}")
    public ResponseEntity<ApiResponse<?>> getCustomerTier(@PathVariable Long customerId) {
        try {
            LoyaltyTier tier = loyaltyService.getCustomerTier(customerId);
            return ResponseEntity.ok(ApiResponse.success(tier, "Customer tier retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the next loyalty tier for a customer.
     *
     * @param customerId the customer ID
     * @return a response entity with the customer's next tier
     */
    @GetMapping("/next-tier/{customerId}")
    public ResponseEntity<ApiResponse<?>> getNextTier(@PathVariable Long customerId) {
        try {
            LoyaltyTier nextTier = loyaltyService.getNextTier(customerId);
            Integer pointsToNextTier = loyaltyService.getPointsToNextTier(customerId);
            
            // Create response with both next tier and points needed
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "nextTier", nextTier,
                    "pointsNeeded", pointsToNextTier
            ), "Next tier information retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Award points to a customer for a purchase.
     *
     * @param requestBody the request body containing customer ID, order ID, and amount
     * @return a response entity with the created points transaction
     */
    @PostMapping("/earn")
    public ResponseEntity<ApiResponse<?>> awardPointsForPurchase(@RequestBody Map<String, Object> requestBody) {
        try {
            Long customerId = Long.valueOf(requestBody.get("customerId").toString());
            Long orderId = Long.valueOf(requestBody.get("orderId").toString());
            Double amount = Double.valueOf(requestBody.get("amount").toString());
            
            LoyaltyPoints points = loyaltyService.awardPointsForPurchase(customerId, orderId, amount);
            return ResponseEntity.ok(ApiResponse.success(points, "Points awarded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Award bonus points to a customer.
     *
     * @param requestBody the request body containing customer ID, points, and description
     * @return a response entity with the created points transaction
     */
    @PostMapping("/bonus")
    public ResponseEntity<ApiResponse<?>> awardBonusPoints(@RequestBody Map<String, Object> requestBody) {
        try {
            Long customerId = Long.valueOf(requestBody.get("customerId").toString());
            Integer points = Integer.valueOf(requestBody.get("points").toString());
            String description = requestBody.get("description").toString();
            
            LoyaltyPoints loyaltyPoints = loyaltyService.awardBonusPoints(customerId, points, description);
            return ResponseEntity.ok(ApiResponse.success(loyaltyPoints, "Bonus points awarded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Redeem points for a reward.
     *
     * @param requestBody the request body containing customer ID and reward ID
     * @return a response entity with the created points transaction
     */
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<?>> redeemPoints(@RequestBody Map<String, Object> requestBody) {
        try {
            Long customerId = Long.valueOf(requestBody.get("customerId").toString());
            Long rewardId = Long.valueOf(requestBody.get("rewardId").toString());
            
            LoyaltyPoints points = loyaltyService.redeemPoints(customerId, rewardId);
            return ResponseEntity.ok(ApiResponse.success(points, "Points redeemed successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the points history for a customer.
     *
     * @param customerId the customer ID
     * @return a response entity with the customer's points history
     */
    @GetMapping("/history/{customerId}")
    public ResponseEntity<ApiResponse<?>> getPointsHistory(@PathVariable Long customerId) {
        try {
            List<LoyaltyPoints> history = loyaltyService.getPointsHistory(customerId);
            return ResponseEntity.ok(ApiResponse.success(history, "Points history retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the points history for a customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a response entity with the customer's points history
     */
    @GetMapping("/history/{customerId}/range")
    public ResponseEntity<ApiResponse<?>> getPointsHistoryByDateRange(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<LoyaltyPoints> history = loyaltyService.getPointsHistory(customerId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(history, "Points history retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all available rewards for a customer.
     *
     * @param customerId the customer ID
     * @return a response entity with the available rewards
     */
    @GetMapping("/rewards/{customerId}")
    public ResponseEntity<ApiResponse<?>> getAvailableRewards(@PathVariable Long customerId) {
        try {
            List<LoyaltyReward> rewards = loyaltyService.getAvailableRewards(customerId);
            return ResponseEntity.ok(ApiResponse.success(rewards, "Available rewards retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all loyalty rewards.
     *
     * @return a response entity with all loyalty rewards
     */
    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<?>> getAllRewards() {
        try {
            List<LoyaltyReward> rewards = loyaltyService.getAvailableRewards(null);
            return ResponseEntity.ok(ApiResponse.success(rewards, "All rewards retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all loyalty tiers.
     *
     * @return a response entity with all loyalty tiers
     */
    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<?>> getAllTiers() {
        try {
            List<LoyaltyTier> tiers = loyaltyService.getAllTiers();
            return ResponseEntity.ok(ApiResponse.success(tiers, "All tiers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create a new loyalty reward.
     *
     * @param reward the loyalty reward to create
     * @return a response entity with the created reward
     */
    @PostMapping("/rewards")
    public ResponseEntity<ApiResponse<?>> createReward(@RequestBody LoyaltyReward reward) {
        try {
            LoyaltyReward createdReward = loyaltyService.createReward(reward);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdReward, "Reward created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update a loyalty reward.
     *
     * @param id the reward ID
     * @param reward the updated loyalty reward
     * @return a response entity with the updated reward
     */
    @PutMapping("/rewards/{id}")
    public ResponseEntity<ApiResponse<?>> updateReward(@PathVariable Long id, @RequestBody LoyaltyReward reward) {
        try {
            LoyaltyReward updatedReward = loyaltyService.updateReward(id, reward);
            return ResponseEntity.ok(ApiResponse.success(updatedReward, "Reward updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a loyalty reward.
     *
     * @param id the reward ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<ApiResponse<?>> deleteReward(@PathVariable Long id) {
        try {
            loyaltyService.deleteReward(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Reward deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create a new loyalty tier.
     *
     * @param tier the loyalty tier to create
     * @return a response entity with the created tier
     */
    @PostMapping("/tiers")
    public ResponseEntity<ApiResponse<?>> createTier(@RequestBody LoyaltyTier tier) {
        try {
            LoyaltyTier createdTier = loyaltyService.createTier(tier);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdTier, "Tier created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update a loyalty tier.
     *
     * @param id the tier ID
     * @param tier the updated loyalty tier
     * @return a response entity with the updated tier
     */
    @PutMapping("/tiers/{id}")
    public ResponseEntity<ApiResponse<?>> updateTier(@PathVariable Long id, @RequestBody LoyaltyTier tier) {
        try {
            LoyaltyTier updatedTier = loyaltyService.updateTier(id, tier);
            return ResponseEntity.ok(ApiResponse.success(updatedTier, "Tier updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a loyalty tier.
     *
     * @param id the tier ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/tiers/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTier(@PathVariable Long id) {
        try {
            loyaltyService.deleteTier(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Tier deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Process expired points.
     *
     * @return a response entity with the number of expired points transactions processed
     */
    @PostMapping("/process-expired")
    public ResponseEntity<ApiResponse<?>> processExpiredPoints() {
        try {
            int count = loyaltyService.processExpiredPoints();
            return ResponseEntity.ok(ApiResponse.success(count, count + " expired points transactions processed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}