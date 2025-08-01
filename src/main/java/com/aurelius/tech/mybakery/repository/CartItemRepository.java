package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity.
 * Provides methods for CRUD operations on CartItem entities.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Find cart items by cart ID.
     *
     * @param cartId the cart ID to search for
     * @return a list of cart items with the given cart ID
     */
    List<CartItem> findByCartId(Long cartId);
    
    /**
     * Find a cart item by cart ID and product ID.
     *
     * @param cartId the cart ID to search for
     * @param productId the product ID to search for
     * @return an Optional containing the cart item if found, or empty if not found
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    /**
     * Delete cart items by cart ID.
     *
     * @param cartId the cart ID to delete items for
     */
    void deleteByCartId(Long cartId);
    
    /**
     * Delete a cart item by cart ID and product ID.
     *
     * @param cartId the cart ID to delete the item for
     * @param productId the product ID to delete the item for
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);
    
    /**
     * Count cart items by cart ID.
     *
     * @param cartId the cart ID to count items for
     * @return the number of items in the cart
     */
    long countByCartId(Long cartId);
}