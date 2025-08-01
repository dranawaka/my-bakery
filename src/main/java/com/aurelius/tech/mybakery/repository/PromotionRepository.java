package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Promotion;
import com.aurelius.tech.mybakery.model.Promotion.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing promotions.
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    /**
     * Find a promotion by its promo code.
     *
     * @param promoCode the promo code
     * @return the promotion with the specified promo code
     */
    Optional<Promotion> findByPromoCode(String promoCode);
    
    /**
     * Find all active promotions.
     *
     * @return a list of active promotions
     */
    List<Promotion> findByIsActiveTrue();
    
    /**
     * Find all active promotions of a specific type.
     *
     * @param discountType the discount type
     * @return a list of active promotions of the specified type
     */
    List<Promotion> findByIsActiveTrueAndDiscountType(DiscountType discountType);
    
    /**
     * Find all active promotions for a specific category.
     *
     * @param categoryId the category ID
     * @return a list of active promotions for the specified category
     */
    List<Promotion> findByIsActiveTrueAndCategoryId(Long categoryId);
    
    /**
     * Find all active promotions for a specific product.
     *
     * @param productId the product ID
     * @return a list of active promotions for the specified product
     */
    List<Promotion> findByIsActiveTrueAndProductId(Long productId);
    
    /**
     * Find all active promotions that are valid at the current time.
     *
     * @param currentTime the current time
     * @return a list of active promotions that are valid at the current time
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :currentTime AND (p.endDate IS NULL OR p.endDate >= :currentTime)")
    List<Promotion> findActiveAndValidPromotions(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find all active promotions that are valid at the current time for a specific category.
     *
     * @param currentTime the current time
     * @param categoryId the category ID
     * @return a list of active promotions that are valid at the current time for the specified category
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :currentTime AND (p.endDate IS NULL OR p.endDate >= :currentTime) AND p.categoryId = :categoryId")
    List<Promotion> findActiveAndValidPromotionsByCategory(@Param("currentTime") LocalDateTime currentTime, @Param("categoryId") Long categoryId);
    
    /**
     * Find all active promotions that are valid at the current time for a specific product.
     *
     * @param currentTime the current time
     * @param productId the product ID
     * @return a list of active promotions that are valid at the current time for the specified product
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :currentTime AND (p.endDate IS NULL OR p.endDate >= :currentTime) AND p.productId = :productId")
    List<Promotion> findActiveAndValidPromotionsByProduct(@Param("currentTime") LocalDateTime currentTime, @Param("productId") Long productId);
    
    /**
     * Find all promotions that are expiring soon.
     *
     * @param currentTime the current time
     * @param expiryTime the expiry time
     * @return a list of promotions that are expiring soon
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.endDate IS NOT NULL AND p.endDate BETWEEN :currentTime AND :expiryTime")
    List<Promotion> findPromotionsExpiringSoon(@Param("currentTime") LocalDateTime currentTime, @Param("expiryTime") LocalDateTime expiryTime);
    
    /**
     * Find all promotions that have a usage limit and have not reached it.
     *
     * @return a list of promotions that have a usage limit and have not reached it
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.usageLimit IS NOT NULL AND p.usageCount < p.usageLimit")
    List<Promotion> findPromotionsWithAvailableUsage();
}