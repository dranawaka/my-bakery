package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cart entity.
 * Provides methods for CRUD operations on Cart entities.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find a cart by user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the cart if found, or empty if not found
     */
    Optional<Cart> findByUserId(Long userId);
    
    /**
     * Find a cart by session ID.
     *
     * @param sessionId the session ID to search for
     * @return an Optional containing the cart if found, or empty if not found
     */
    Optional<Cart> findBySessionId(String sessionId);
    
    /**
     * Find expired carts.
     *
     * @param expiryDate the date to compare with
     * @return a list of carts with expiry date before the given date
     */
    List<Cart> findByExpiresAtBefore(LocalDateTime expiryDate);
    
    /**
     * Find carts by user ID and expiry date.
     *
     * @param userId the user ID to search for
     * @param expiryDate the date to compare with
     * @return a list of carts with the given user ID and expiry date after the given date
     */
    List<Cart> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime expiryDate);
    
    /**
     * Find carts by session ID and expiry date.
     *
     * @param sessionId the session ID to search for
     * @param expiryDate the date to compare with
     * @return a list of carts with the given session ID and expiry date after the given date
     */
    List<Cart> findBySessionIdAndExpiresAtAfter(String sessionId, LocalDateTime expiryDate);
    
    /**
     * Delete expired carts.
     *
     * @param expiryDate the date to compare with
     */
    void deleteByExpiresAtBefore(LocalDateTime expiryDate);
}