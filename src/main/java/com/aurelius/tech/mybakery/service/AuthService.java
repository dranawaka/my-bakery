package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for handling authentication and authorization.
 * This is a simplified version without Spring Security or JWT dependencies.
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependencies.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Register a new user.
     *
     * @param user the user to register
     * @return the registered user
     * @throws RuntimeException if a user with the same email already exists
     */
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        // In a real implementation, we would encode the password here
        user.setRole(User.Role.CUSTOMER); // Default role for new users
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate a user.
     *
     * @param email the user's email
     * @param password the user's password
     * @return the authenticated user
     * @throws RuntimeException if authentication fails
     */
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOptional.get();
        // In a real implementation, we would check the password here
        // For now, we'll just assume the password is correct
        
        return user;
    }
    
    /**
     * Get a user by ID.
     *
     * @param id the user's ID
     * @return the user
     * @throws RuntimeException if the user is not found
     */
    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOptional.get();
    }
    
    /**
     * Get a user by email.
     *
     * @param email the user's email
     * @return the user
     * @throws RuntimeException if the user is not found
     */
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOptional.get();
    }
    
    /**
     * Change a user's password.
     *
     * @param userId the user's ID
     * @param oldPassword the user's old password
     * @param newPassword the user's new password
     * @return the updated user
     * @throws RuntimeException if the user is not found or the old password is incorrect
     */
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        // In a real implementation, we would check the old password here
        // For now, we'll just assume the old password is correct
        
        // In a real implementation, we would encode the new password here
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Reset a user's password.
     *
     * @param email the user's email
     * @param newPassword the user's new password
     * @return the updated user
     * @throws RuntimeException if the user is not found
     */
    public User resetPassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        // In a real implementation, we would encode the new password here
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
}