package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Promotion;
import com.aurelius.tech.mybakery.model.Promotion.DiscountType;
import com.aurelius.tech.mybakery.model.PromotionUsage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing promotions and discounts.
 */
public interface PromotionService {
    
    /**
     * Get all promotions.
     *
     * @return all promotions
     */
    List<Promotion> getAllPromotions();
    
    /**
     * Get a promotion by ID.
     *
     * @param id the promotion ID
     * @return the promotion with the specified ID
     */
    Promotion getPromotionById(Long id);
    
    /**
     * Get a promotion by promo code.
     *
     * @param promoCode the promo code
     * @return the promotion with the specified promo code
     */
    Promotion getPromotionByPromoCode(String promoCode);
    
    /**
     * Get all active promotions.
     *
     * @return all active promotions
     */
    List<Promotion> getActivePromotions();
    
    /**
     * Get all active promotions of a specific type.
     *
     * @param discountType the discount type
     * @return all active promotions of the specified type
     */
    List<Promotion> getActivePromotionsByType(DiscountType discountType);
    
    /**
     * Get all active promotions for a specific category.
     *
     * @param categoryId the category ID
     * @return all active promotions for the specified category
     */
    List<Promotion> getActivePromotionsByCategory(Long categoryId);
    
    /**
     * Get all active promotions for a specific product.
     *
     * @param productId the product ID
     * @return all active promotions for the specified product
     */
    List<Promotion> getActivePromotionsByProduct(Long productId);
    
    /**
     * Get all active and valid promotions.
     *
     * @return all active and valid promotions
     */
    List<Promotion> getActiveAndValidPromotions();
    
    /**
     * Get all promotions that are expiring soon (within the next 7 days).
     *
     * @return all promotions that are expiring soon
     */
    List<Promotion> getPromotionsExpiringSoon();
    
    /**
     * Create a new promotion.
     *
     * @param promotion the promotion to create
     * @return the created promotion
     */
    Promotion createPromotion(Promotion promotion);
    
    /**
     * Update an existing promotion.
     *
     * @param id the promotion ID
     * @param promotion the updated promotion
     * @return the updated promotion
     */
    Promotion updatePromotion(Long id, Promotion promotion);
    
    /**
     * Delete a promotion.
     *
     * @param id the promotion ID
     */
    void deletePromotion(Long id);
    
    /**
     * Activate a promotion.
     *
     * @param id the promotion ID
     * @return the activated promotion
     */
    Promotion activatePromotion(Long id);
    
    /**
     * Deactivate a promotion.
     *
     * @param id the promotion ID
     * @return the deactivated promotion
     */
    Promotion deactivatePromotion(Long id);
    
    /**
     * Validate a promotion for a customer and order total.
     *
     * @param promoCode the promo code
     * @param customerId the customer ID
     * @param orderTotal the order total
     * @return true if the promotion is valid, false otherwise
     */
    boolean validatePromotion(String promoCode, Long customerId, BigDecimal orderTotal);
    
    /**
     * Apply a promotion to an order.
     *
     * @param promoCode the promo code
     * @param customerId the customer ID
     * @param orderId the order ID
     * @param orderTotal the order total
     * @return the discount amount
     */
    BigDecimal applyPromotion(String promoCode, Long customerId, Long orderId, BigDecimal orderTotal);
    
    /**
     * Get all promotion usages.
     *
     * @return all promotion usages
     */
    List<PromotionUsage> getAllPromotionUsages();
    
    /**
     * Get all promotion usages for a specific promotion.
     *
     * @param promotionId the promotion ID
     * @return all promotion usages for the specified promotion
     */
    List<PromotionUsage> getPromotionUsagesByPromotion(Long promotionId);
    
    /**
     * Get all promotion usages for a specific customer.
     *
     * @param customerId the customer ID
     * @return all promotion usages for the specified customer
     */
    List<PromotionUsage> getPromotionUsagesByCustomer(Long customerId);
    
    /**
     * Get all promotion usages for a specific order.
     *
     * @param orderId the order ID
     * @return all promotion usages for the specified order
     */
    List<PromotionUsage> getPromotionUsagesByOrder(Long orderId);
    
    /**
     * Get all promotion usages within a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return all promotion usages within the specified date range
     */
    List<PromotionUsage> getPromotionUsagesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count the number of times a customer has used a specific promotion.
     *
     * @param promotionId the promotion ID
     * @param customerId the customer ID
     * @return the number of times the customer has used the promotion
     */
    int countPromotionUsagesByCustomer(Long promotionId, Long customerId);
}