package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.Cart;
import com.aurelius.tech.mybakery.service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling shopping cart endpoints.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;
    private static final String CART_SESSION_COOKIE = "cart_session_id";
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30 days in seconds
    
    /**
     * Constructor with dependencies.
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * Get the current user's cart.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @return a response entity with the cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCart(
            @RequestHeader(value = "Authorization", required = false) String token,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Cart cart;
            
            if (token != null && !token.isEmpty()) {
                // Authenticated user - get user ID from token
                // In a real implementation, we would extract the user ID from the JWT token
                Long userId = 1L; // Simulated user ID
                cart = cartService.getUserCart(userId);
            } else {
                // Guest user - use session ID from cookie
                String sessionId = getCartSessionId(request);
                
                if (sessionId == null) {
                    // Create new session ID
                    sessionId = cartService.generateSessionId();
                    setCartSessionCookie(response, sessionId);
                }
                
                cart = cartService.getGuestCart(sessionId);
            }
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Add an item to the cart.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param requestBody the request body containing product ID and quantity
     * @return a response entity with the updated cart
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<?>> addItemToCart(
            @RequestHeader(value = "Authorization", required = false) String token,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody Map<String, Object> requestBody) {
        try {
            // Extract product ID and quantity from request body
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
            
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Quantity must be greater than zero"));
            }
            
            Cart cart;
            
            if (token != null && !token.isEmpty()) {
                // Authenticated user - get user ID from token
                // In a real implementation, we would extract the user ID from the JWT token
                Long userId = 1L; // Simulated user ID
                cart = cartService.getUserCart(userId);
            } else {
                // Guest user - use session ID from cookie
                String sessionId = getCartSessionId(request);
                
                if (sessionId == null) {
                    // Create new session ID
                    sessionId = cartService.generateSessionId();
                    setCartSessionCookie(response, sessionId);
                }
                
                cart = cartService.getGuestCart(sessionId);
            }
            
            // Add item to cart
            cart = cartService.addItemToCart(cart.getId(), productId, quantity);
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update the quantity of an item in the cart.
     *
     * @param itemId the item ID
     * @param requestBody the request body containing the new quantity
     * @return a response entity with the updated cart
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            // Extract quantity from request body
            Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
            
            // In a real implementation, we would get the cart ID from the authenticated user or session
            Long cartId = 1L; // Simulated cart ID
            
            // Update item quantity
            Cart cart = cartService.updateCartItemQuantity(cartId, itemId, quantity);
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Cart item updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Remove an item from the cart.
     *
     * @param itemId the item ID
     * @return a response entity with the updated cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeItemFromCart(@PathVariable Long itemId) {
        try {
            // In a real implementation, we would get the cart ID from the authenticated user or session
            Long cartId = 1L; // Simulated cart ID
            
            // Remove item from cart
            Cart cart = cartService.removeItemFromCart(cartId, itemId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Item removed from cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Clear the cart.
     *
     * @return a response entity with the updated cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<?>> clearCart() {
        try {
            // In a real implementation, we would get the cart ID from the authenticated user or session
            Long cartId = 1L; // Simulated cart ID
            
            // Clear cart
            Cart cart = cartService.clearCart(cartId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Merge a guest cart into a user cart.
     *
     * @param request the HTTP request
     * @return a response entity with the merged cart
     */
    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<?>> mergeGuestCartIntoUserCart(
            HttpServletRequest request) {
        try {
            // Get session ID from cookie
            String sessionId = getCartSessionId(request);
            
            if (sessionId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No guest cart found"));
            }
            
            // Get guest cart
            Cart guestCart = cartService.getGuestCart(sessionId);
            
            // In a real implementation, we would get the user ID from the authenticated user
            Long userId = 1L; // Simulated user ID
            
            // Get user cart
            Cart userCart = cartService.getUserCart(userId);
            
            // Merge carts
            Cart mergedCart = cartService.mergeGuestCartIntoUserCart(guestCart.getId(), userCart.getId());
            
            return ResponseEntity.ok(ApiResponse.success(mergedCart, "Carts merged successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get the cart session ID from the request cookies.
     *
     * @param request the HTTP request
     * @return the cart session ID, or null if not found
     */
    private String getCartSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CART_SESSION_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Set the cart session ID cookie in the response.
     *
     * @param response the HTTP response
     * @param sessionId the cart session ID
     */
    private void setCartSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(CART_SESSION_COOKIE, sessionId);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}