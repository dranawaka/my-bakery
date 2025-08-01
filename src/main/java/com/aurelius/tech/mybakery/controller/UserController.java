package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.model.Address;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling user-related endpoints.
 * This is a simplified version without Spring Security dependencies.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Constructor with dependencies.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get the current user's profile.
     *
     * @return a response entity with the user's profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            // In a real implementation, we would extract the user ID from the JWT token
            // For now, we'll just use a simulated user ID
            Long userId = 1L;
            User user = userService.getUserById(userId);
            
            return createSuccessResponse("User profile retrieved successfully", user);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update the current user's profile.
     *
     * @param user the updated user information
     * @return a response entity with the updated user's profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody User user) {
        try {
            // In a real implementation, we would extract the user ID from the JWT token
            // For now, we'll just use a simulated user ID
            Long userId = 1L;
            User updatedUser = userService.updateUserProfile(userId, user);
            
            return createSuccessResponse("User profile updated successfully", updatedUser);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return a response entity with the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return createSuccessResponse("User retrieved successfully", user);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Update a user.
     *
     * @param id the user ID
     * @param user the updated user information
     * @return a response entity with the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUserProfile(id, user);
            return createSuccessResponse("User updated successfully", updatedUser);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a user.
     *
     * @param id the user ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return createSuccessResponse("User deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get all users.
     *
     * @return a response entity with all users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return createSuccessResponse("Users retrieved successfully", users);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Add an address to a user.
     *
     * @param address the address to add
     * @return a response entity with the added address
     */
    @PostMapping("/addresses")
    public ResponseEntity<?> addUserAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        try {
            // In a real implementation, we would extract the user ID from the JWT token
            // For now, we'll just use a simulated user ID
            Long userId = 1L;
            Address addedAddress = userService.addUserAddress(userId, address);
            
            return createSuccessResponse("Address added successfully", addedAddress);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a user's addresses.
     *
     * @return a response entity with the user's addresses
     */
    @GetMapping("/addresses")
    public ResponseEntity<?> getUserAddresses(@RequestHeader("Authorization") String token) {
        try {
            // In a real implementation, we would extract the user ID from the JWT token
            // For now, we'll just use a simulated user ID
            Long userId = 1L;
            List<Address> addresses = userService.getUserAddresses(userId);
            
            return createSuccessResponse("Addresses retrieved successfully", addresses);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update a user's address.
     *
     * @param id the address ID
     * @param address the updated address information
     * @return a response entity with the updated address
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateUserAddress(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Address address) {
        try {
            // In a real implementation, we would update the address
            // For now, we'll just return the address with the updated ID
            address.setId(id);
            
            return createSuccessResponse("Address updated successfully", address);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a user's address.
     *
     * @param id the address ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteUserAddress(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            // In a real implementation, we would delete the address
            // For now, we'll just return a success message
            return createSuccessResponse("Address deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Create a success response.
     *
     * @param message the success message
     * @param data the response data
     * @return a response entity with the success response
     */
    private ResponseEntity<?> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create an error response.
     *
     * @param message the error message
     * @param status the HTTP status
     * @return a response entity with the error response
     */
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", Map.of("message", message));
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(status).body(response);
    }
}