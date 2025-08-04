package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Cart;
import com.aurelius.tech.mybakery.model.CartItem;
import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.CartItemRepository;
import com.aurelius.tech.mybakery.repository.CartRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling shopping cart operations.
 */
@Service
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependencies.
     */
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                      ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Get a cart by ID.
     *
     * @param id the cart ID
     * @return the cart
     * @throws RuntimeException if the cart is not found
     */
    public Cart getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        // Load cart items with products
        List<CartItem> items = cartItemRepository.findByCartIdWithProduct(id);
        cart.setItems(items);
        
        return cart;
    }
    
    /**
     * Get a user's cart.
     *
     * @param userId the user ID
     * @return the user's cart, or a new cart if not found
     */
    public Cart getUserCart(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            // Check if cart is expired
            if (cart.isExpired()) {
                // Clear expired cart and reset expiration
                cart.clear();
                cart.setExpiresAt(LocalDateTime.now().plusDays(30));
                cart.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(cart);
            }
            
            // Load cart items with products
            List<CartItem> items = cartItemRepository.findByCartIdWithProduct(cart.getId());
            cart.setItems(items);
            
            return cart;
        } else {
            // Create new cart for user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart.setExpiresAt(LocalDateTime.now().plusDays(30));
            
            return cartRepository.save(cart);
        }
    }
    
    /**
     * Get a guest cart by session ID.
     *
     * @param sessionId the session ID
     * @return the guest cart, or a new cart if not found
     */
    public Cart getGuestCart(String sessionId) {
        Optional<Cart> cartOptional = cartRepository.findBySessionId(sessionId);
        
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            // Check if cart is expired
            if (cart.isExpired()) {
                // Clear expired cart and reset expiration
                cart.clear();
                cart.setExpiresAt(LocalDateTime.now().plusDays(30));
                cart.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(cart);
            }
            
            // Load cart items with products
            List<CartItem> items = cartItemRepository.findByCartIdWithProduct(cart.getId());
            cart.setItems(items);
            
            return cart;
        } else {
            // Create new cart for guest
            Cart cart = new Cart();
            cart.setSessionId(sessionId);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart.setExpiresAt(LocalDateTime.now().plusDays(30));
            
            return cartRepository.save(cart);
        }
    }
    
    /**
     * Add an item to a cart.
     *
     * @param cartId the cart ID
     * @param productId the product ID
     * @param quantity the quantity to add
     * @return the updated cart
     * @throws RuntimeException if the cart or product is not found
     */
    @Transactional
    public Cart addItemToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = getCartById(cartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product is active
        if (!product.isActive()) {
            throw new RuntimeException("Product is not available");
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItemOptional = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        
        if (existingItemOptional.isPresent()) {
            // Update existing item
            CartItem existingItem = existingItemOptional.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(existingItem);
        } else {
            // Create new item
            CartItem newItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(newItem);
        }
        
        // Reload cart with items and update
        cart = getCartById(cartId);
        cart.recalculateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    /**
     * Update the quantity of an item in a cart.
     *
     * @param cartId the cart ID
     * @param itemId the item ID
     * @param quantity the new quantity
     * @return the updated cart
     * @throws RuntimeException if the cart or item is not found
     */
    @Transactional
    public Cart updateCartItemQuantity(Long cartId, Long itemId, Integer quantity) {
        Cart cart = getCartById(cartId);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Ensure item belongs to the specified cart
        if (!item.getCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to the specified cart");
        }
        
        // Remove item if quantity is 0 or less
        if (quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            item.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(item);
        }
        
        // Update cart
        cart.recalculateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    /**
     * Remove an item from a cart.
     *
     * @param cartId the cart ID
     * @param itemId the item ID
     * @return the updated cart
     * @throws RuntimeException if the cart or item is not found
     */
    @Transactional
    public Cart removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = getCartById(cartId);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Ensure item belongs to the specified cart
        if (!item.getCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to the specified cart");
        }
        
        cart.removeItem(item);
        cartItemRepository.delete(item);
        
        // Update cart
        cart.recalculateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    /**
     * Save an item for later.
     *
     * @param cartId the cart ID
     * @param itemId the item ID
     * @return the updated cart
     * @throws RuntimeException if the cart or item is not found
     */
    @Transactional
    public Cart saveItemForLater(Long cartId, Long itemId) {
        Cart cart = getCartById(cartId);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Ensure item belongs to the specified cart
        if (!item.getCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to the specified cart");
        }
        
        // Toggle saved for later status
        item.setSavedForLater(!item.getSavedForLater());
        item.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(item);
        
        // Update cart
        cart.recalculateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    /**
     * Clear a cart.
     *
     * @param cartId the cart ID
     * @return the updated cart
     * @throws RuntimeException if the cart is not found
     */
    @Transactional
    public Cart clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        
        // Delete all items
        cartItemRepository.deleteByCartId(cartId);
        
        // Clear cart
        cart.clear();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    /**
     * Merge a guest cart into a user cart.
     *
     * @param guestCartId the guest cart ID
     * @param userCartId the user cart ID
     * @return the merged user cart
     * @throws RuntimeException if either cart is not found
     */
    @Transactional
    public Cart mergeGuestCartIntoUserCart(Long guestCartId, Long userCartId) {
        Cart guestCart = getCartById(guestCartId);
        Cart userCart = getCartById(userCartId);
        
        // Get all items from guest cart
        List<CartItem> guestItems = cartItemRepository.findByCartId(guestCartId);
        
        // Add each guest item to user cart
        for (CartItem guestItem : guestItems) {
            Optional<CartItem> existingItemOptional = cartItemRepository.findByCartIdAndProductId(
                    userCartId, guestItem.getProduct().getId());
            
            if (existingItemOptional.isPresent()) {
                // Update existing item in user cart
                CartItem existingItem = existingItemOptional.get();
                existingItem.setQuantity(existingItem.getQuantity() + guestItem.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                cartItemRepository.save(existingItem);
            } else {
                // Create new item in user cart
                CartItem newItem = new CartItem(userCart, guestItem.getProduct(), guestItem.getQuantity());
                userCart.addItem(newItem);
                cartItemRepository.save(newItem);
            }
        }
        
        // Clear guest cart
        clearCart(guestCartId);
        
        // Update user cart
        userCart.recalculateTotalAmount();
        userCart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(userCart);
    }
    
    /**
     * Generate a unique session ID for guest carts.
     *
     * @return a unique session ID
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Clean up expired carts.
     */
    @Transactional
    public void cleanupExpiredCarts() {
        LocalDateTime now = LocalDateTime.now();
        cartRepository.deleteByExpiresAtBefore(now);
    }
}