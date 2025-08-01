package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.exception.ResourceNotFoundException;
import com.aurelius.tech.mybakery.model.Promotion;
import com.aurelius.tech.mybakery.model.Promotion.DiscountType;
import com.aurelius.tech.mybakery.model.PromotionUsage;
import com.aurelius.tech.mybakery.repository.PromotionRepository;
import com.aurelius.tech.mybakery.repository.PromotionUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the PromotionService interface.
 */
@Service
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public PromotionServiceImpl(
            PromotionRepository promotionRepository,
            PromotionUsageRepository promotionUsageRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionUsageRepository = promotionUsageRepository;
    }
    
    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }
    
    @Override
    public Promotion getPromotionByPromoCode(String promoCode) {
        return promotionRepository.findByPromoCode(promoCode)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + promoCode));
    }
    
    @Override
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }
    
    @Override
    public List<Promotion> getActivePromotionsByType(DiscountType discountType) {
        return promotionRepository.findByIsActiveTrueAndDiscountType(discountType);
    }
    
    @Override
    public List<Promotion> getActivePromotionsByCategory(Long categoryId) {
        return promotionRepository.findByIsActiveTrueAndCategoryId(categoryId);
    }
    
    @Override
    public List<Promotion> getActivePromotionsByProduct(Long productId) {
        return promotionRepository.findByIsActiveTrueAndProductId(productId);
    }
    
    @Override
    public List<Promotion> getActiveAndValidPromotions() {
        return promotionRepository.findActiveAndValidPromotions(LocalDateTime.now());
    }
    
    @Override
    public List<Promotion> getPromotionsExpiringSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plus(7, ChronoUnit.DAYS);
        return promotionRepository.findPromotionsExpiringSoon(now, sevenDaysLater);
    }
    
    @Override
    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        // Validate promo code uniqueness if provided
        if (promotion.getPromoCode() != null && !promotion.getPromoCode().isEmpty()) {
            Optional<Promotion> existingPromotion = promotionRepository.findByPromoCode(promotion.getPromoCode());
            if (existingPromotion.isPresent()) {
                throw new IllegalArgumentException("Promotion with code " + promotion.getPromoCode() + " already exists");
            }
        }
        
        return promotionRepository.save(promotion);
    }
    
    @Override
    @Transactional
    public Promotion updatePromotion(Long id, Promotion promotion) {
        Promotion existingPromotion = getPromotionById(id);
        
        // Validate promo code uniqueness if changed
        if (promotion.getPromoCode() != null && !promotion.getPromoCode().equals(existingPromotion.getPromoCode())) {
            Optional<Promotion> promoWithSameCode = promotionRepository.findByPromoCode(promotion.getPromoCode());
            if (promoWithSameCode.isPresent() && !promoWithSameCode.get().getId().equals(id)) {
                throw new IllegalArgumentException("Promotion with code " + promotion.getPromoCode() + " already exists");
            }
        }
        
        // Update fields
        existingPromotion.setName(promotion.getName());
        existingPromotion.setDescription(promotion.getDescription());
        existingPromotion.setPromoCode(promotion.getPromoCode());
        existingPromotion.setDiscountType(promotion.getDiscountType());
        existingPromotion.setDiscountValue(promotion.getDiscountValue());
        existingPromotion.setMinimumOrderValue(promotion.getMinimumOrderValue());
        existingPromotion.setMaximumDiscount(promotion.getMaximumDiscount());
        existingPromotion.setUsageLimit(promotion.getUsageLimit());
        existingPromotion.setStartDate(promotion.getStartDate());
        existingPromotion.setEndDate(promotion.getEndDate());
        existingPromotion.setIsActive(promotion.getIsActive());
        existingPromotion.setCategoryId(promotion.getCategoryId());
        existingPromotion.setProductId(promotion.getProductId());
        
        return promotionRepository.save(existingPromotion);
    }
    
    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }
    
    @Override
    @Transactional
    public Promotion activatePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(true);
        return promotionRepository.save(promotion);
    }
    
    @Override
    @Transactional
    public Promotion deactivatePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(false);
        return promotionRepository.save(promotion);
    }
    
    @Override
    public boolean validatePromotion(String promoCode, Long customerId, BigDecimal orderTotal) {
        try {
            Promotion promotion = getPromotionByPromoCode(promoCode);
            
            // Check if promotion is active and valid
            if (!promotion.isValid()) {
                return false;
            }
            
            // Check if minimum order value is met
            if (promotion.getMinimumOrderValue() != null && 
                orderTotal.compareTo(promotion.getMinimumOrderValue()) < 0) {
                return false;
            }
            
            // Check if customer has reached usage limit (if applicable)
            if (promotion.getUsageLimit() != null) {
                int usageCount = promotionUsageRepository.countByPromotionIdAndCustomerId(promotion.getId(), customerId);
                if (usageCount >= promotion.getUsageLimit()) {
                    return false;
                }
            }
            
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public BigDecimal applyPromotion(String promoCode, Long customerId, Long orderId, BigDecimal orderTotal) {
        if (!validatePromotion(promoCode, customerId, orderTotal)) {
            throw new IllegalArgumentException("Promotion is not valid");
        }
        
        Promotion promotion = getPromotionByPromoCode(promoCode);
        BigDecimal discountAmount = promotion.calculateDiscount(orderTotal);
        
        // Create usage record
        PromotionUsage usage = new PromotionUsage();
        usage.setPromotionId(promotion.getId());
        usage.setCustomerId(customerId);
        usage.setOrderId(orderId);
        usage.setDiscountAmount(discountAmount);
        promotionUsageRepository.save(usage);
        
        // Increment usage count
        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
        
        return discountAmount;
    }
    
    @Override
    public List<PromotionUsage> getAllPromotionUsages() {
        return promotionUsageRepository.findAll();
    }
    
    @Override
    public List<PromotionUsage> getPromotionUsagesByPromotion(Long promotionId) {
        return promotionUsageRepository.findByPromotionId(promotionId);
    }
    
    @Override
    public List<PromotionUsage> getPromotionUsagesByCustomer(Long customerId) {
        return promotionUsageRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<PromotionUsage> getPromotionUsagesByOrder(Long orderId) {
        return promotionUsageRepository.findByOrderId(orderId);
    }
    
    @Override
    public List<PromotionUsage> getPromotionUsagesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return promotionUsageRepository.findByUsedAtBetween(startDate, endDate);
    }
    
    @Override
    public int countPromotionUsagesByCustomer(Long promotionId, Long customerId) {
        return promotionUsageRepository.countByPromotionIdAndCustomerId(promotionId, customerId);
    }
}