package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.Promotion;
import com.aurelius.tech.mybakery.model.Promotion.DiscountType;
import com.aurelius.tech.mybakery.model.PromotionUsage;
import com.aurelius.tech.mybakery.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling promotion and discount endpoints.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/promotions")
public class PromotionController {
    
    private final PromotionService promotionService;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }
    
    /**
     * Get all promotions.
     *
     * @return a response entity with all promotions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPromotions() {
        try {
            List<Promotion> promotions = promotionService.getAllPromotions();
            return ResponseEntity.ok(ApiResponse.success(promotions, "Promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a promotion by ID.
     *
     * @param id the promotion ID
     * @return a response entity with the promotion
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getPromotionById(@PathVariable Long id) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a promotion by promo code.
     *
     * @param promoCode the promo code
     * @return a response entity with the promotion
     */
    @GetMapping("/code/{promoCode}")
    public ResponseEntity<ApiResponse<?>> getPromotionByPromoCode(@PathVariable String promoCode) {
        try {
            Promotion promotion = promotionService.getPromotionByPromoCode(promoCode);
            return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all active promotions.
     *
     * @return a response entity with all active promotions
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getActivePromotions() {
        try {
            List<Promotion> promotions = promotionService.getActivePromotions();
            return ResponseEntity.ok(ApiResponse.success(promotions, "Active promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all active promotions of a specific type.
     *
     * @param discountType the discount type
     * @return a response entity with all active promotions of the specified type
     */
    @GetMapping("/type/{discountType}")
    public ResponseEntity<ApiResponse<?>> getActivePromotionsByType(@PathVariable DiscountType discountType) {
        try {
            List<Promotion> promotions = promotionService.getActivePromotionsByType(discountType);
            return ResponseEntity.ok(ApiResponse.success(promotions, "Promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all active promotions for a specific category.
     *
     * @param categoryId the category ID
     * @return a response entity with all active promotions for the specified category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getActivePromotionsByCategory(@PathVariable Long categoryId) {
        try {
            List<Promotion> promotions = promotionService.getActivePromotionsByCategory(categoryId);
            return ResponseEntity.ok(ApiResponse.success(promotions, "Promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all active promotions for a specific product.
     *
     * @param productId the product ID
     * @return a response entity with all active promotions for the specified product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getActivePromotionsByProduct(@PathVariable Long productId) {
        try {
            List<Promotion> promotions = promotionService.getActivePromotionsByProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(promotions, "Promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all active and valid promotions.
     *
     * @return a response entity with all active and valid promotions
     */
    @GetMapping("/valid")
    public ResponseEntity<ApiResponse<?>> getActiveAndValidPromotions() {
        try {
            List<Promotion> promotions = promotionService.getActiveAndValidPromotions();
            return ResponseEntity.ok(ApiResponse.success(promotions, "Valid promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotions that are expiring soon.
     *
     * @return a response entity with all promotions that are expiring soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<ApiResponse<?>> getPromotionsExpiringSoon() {
        try {
            List<Promotion> promotions = promotionService.getPromotionsExpiringSoon();
            return ResponseEntity.ok(ApiResponse.success(promotions, "Expiring promotions retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Create a new promotion.
     *
     * @param promotion the promotion to create
     * @return a response entity with the created promotion
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPromotion(@RequestBody Promotion promotion) {
        try {
            Promotion createdPromotion = promotionService.createPromotion(promotion);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdPromotion, "Promotion created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update an existing promotion.
     *
     * @param id the promotion ID
     * @param promotion the updated promotion
     * @return a response entity with the updated promotion
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        try {
            Promotion updatedPromotion = promotionService.updatePromotion(id, promotion);
            return ResponseEntity.ok(ApiResponse.success(updatedPromotion, "Promotion updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a promotion.
     *
     * @param id the promotion ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deletePromotion(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Activate a promotion.
     *
     * @param id the promotion ID
     * @return a response entity with the activated promotion
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<?>> activatePromotion(@PathVariable Long id) {
        try {
            Promotion promotion = promotionService.activatePromotion(id);
            return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Deactivate a promotion.
     *
     * @param id the promotion ID
     * @return a response entity with the deactivated promotion
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivatePromotion(@PathVariable Long id) {
        try {
            Promotion promotion = promotionService.deactivatePromotion(id);
            return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Validate a promotion for a customer and order total.
     *
     * @param requestBody the request body containing promo code, customer ID, and order total
     * @return a response entity with the validation result
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validatePromotion(@RequestBody Map<String, Object> requestBody) {
        try {
            String promoCode = requestBody.get("promoCode").toString();
            Long customerId = Long.valueOf(requestBody.get("customerId").toString());
            BigDecimal orderTotal = new BigDecimal(requestBody.get("orderTotal").toString());
            
            boolean isValid = promotionService.validatePromotion(promoCode, customerId, orderTotal);
            
            return ResponseEntity.ok(ApiResponse.success(isValid, 
                    isValid ? "Promotion is valid" : "Promotion is not valid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Apply a promotion to an order.
     *
     * @param requestBody the request body containing promo code, customer ID, order ID, and order total
     * @return a response entity with the discount amount
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<?>> applyPromotion(@RequestBody Map<String, Object> requestBody) {
        try {
            String promoCode = requestBody.get("promoCode").toString();
            Long customerId = Long.valueOf(requestBody.get("customerId").toString());
            Long orderId = Long.valueOf(requestBody.get("orderId").toString());
            BigDecimal orderTotal = new BigDecimal(requestBody.get("orderTotal").toString());
            
            BigDecimal discountAmount = promotionService.applyPromotion(promoCode, customerId, orderId, orderTotal);
            
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "discountAmount", discountAmount,
                    "finalAmount", orderTotal.subtract(discountAmount)
            ), "Promotion applied successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotion usages.
     *
     * @return a response entity with all promotion usages
     */
    @GetMapping("/usages")
    public ResponseEntity<ApiResponse<?>> getAllPromotionUsages() {
        try {
            List<PromotionUsage> usages = promotionService.getAllPromotionUsages();
            return ResponseEntity.ok(ApiResponse.success(usages, "Promotion usages retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotion usages for a specific promotion.
     *
     * @param promotionId the promotion ID
     * @return a response entity with all promotion usages for the specified promotion
     */
    @GetMapping("/usages/promotion/{promotionId}")
    public ResponseEntity<ApiResponse<?>> getPromotionUsagesByPromotion(@PathVariable Long promotionId) {
        try {
            List<PromotionUsage> usages = promotionService.getPromotionUsagesByPromotion(promotionId);
            return ResponseEntity.ok(ApiResponse.success(usages, "Promotion usages retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotion usages for a specific customer.
     *
     * @param customerId the customer ID
     * @return a response entity with all promotion usages for the specified customer
     */
    @GetMapping("/usages/customer/{customerId}")
    public ResponseEntity<ApiResponse<?>> getPromotionUsagesByCustomer(@PathVariable Long customerId) {
        try {
            List<PromotionUsage> usages = promotionService.getPromotionUsagesByCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success(usages, "Promotion usages retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotion usages for a specific order.
     *
     * @param orderId the order ID
     * @return a response entity with all promotion usages for the specified order
     */
    @GetMapping("/usages/order/{orderId}")
    public ResponseEntity<ApiResponse<?>> getPromotionUsagesByOrder(@PathVariable Long orderId) {
        try {
            List<PromotionUsage> usages = promotionService.getPromotionUsagesByOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(usages, "Promotion usages retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all promotion usages within a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a response entity with all promotion usages within the specified date range
     */
    @GetMapping("/usages/date-range")
    public ResponseEntity<ApiResponse<?>> getPromotionUsagesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<PromotionUsage> usages = promotionService.getPromotionUsagesByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(usages, "Promotion usages retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Count the number of times a customer has used a specific promotion.
     *
     * @param promotionId the promotion ID
     * @param customerId the customer ID
     * @return a response entity with the usage count
     */
    @GetMapping("/usages/count/{promotionId}/{customerId}")
    public ResponseEntity<ApiResponse<?>> countPromotionUsagesByCustomer(
            @PathVariable Long promotionId, @PathVariable Long customerId) {
        try {
            int count = promotionService.countPromotionUsagesByCustomer(promotionId, customerId);
            return ResponseEntity.ok(ApiResponse.success(count, "Promotion usage count retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}