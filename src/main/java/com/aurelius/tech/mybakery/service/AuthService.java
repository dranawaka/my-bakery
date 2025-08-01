package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for handling authentication and authorization.
 * This is a simplified version without Spring Security or JWT dependencies.
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${admin.username}")
    private String adminUsername;
    
    @Value("${admin.password}")
    private String adminPassword;
    
    /**
     * Constructor with dependencies.
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        // Check if this is the admin user from properties
        if (email.equals(adminUsername)) {
            logger.info("Admin login attempt with username: {}", email);
            
            // Create admin user if it doesn't exist in the database
            Optional<User> adminUserOpt = userRepository.findByEmail(adminUsername);
            User adminUser;
            
            if (adminUserOpt.isEmpty()) {
                logger.info("Creating new admin user in database");
                // Create a new admin user
                adminUser = new User();
                adminUser.setEmail(adminUsername);
                adminUser.setPassword(passwordEncoder.encode(adminPassword)); // Encode the password
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setRole(User.Role.ADMIN);
                adminUser.setEmailVerified(true);
                adminUser.setActive(true);
                adminUser.setCreatedAt(LocalDateTime.now());
                adminUser.setUpdatedAt(LocalDateTime.now());
                adminUser = userRepository.save(adminUser);
            } else {
                adminUser = adminUserOpt.get();
                // Check if admin password matches
                if (!passwordEncoder.matches(adminPassword, adminUser.getPassword())) {
                    logger.info("Updating admin password in database");
                    // Update admin password if it has changed in properties
                    adminUser.setPassword(passwordEncoder.encode(adminPassword));
                    adminUser.setUpdatedAt(LocalDateTime.now());
                    adminUser = userRepository.save(adminUser);
                }
            }
            
            // Verify the provided password
            if (password.equals(adminPassword)) {
                logger.info("Admin login successful");
                return adminUser;
            } else {
                logger.warn("Admin login failed: incorrect password");
                throw new RuntimeException("Invalid email or password. Please try again.");
            }
        }
        
        // Regular user authentication
        logger.info("Regular user login attempt with email: {}", email);
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Login failed: user not found with email: {}", email);
            throw new RuntimeException("Invalid email or password. Please try again.");
        }
        
        User user = userOptional.get();
        // Check if password matches using passwordEncoder
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: incorrect password for user: {}", email);
            throw new RuntimeException("Invalid email or password. Please try again.");
        }
        
        logger.info("User login successful for: {}", email);
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
        // Check if old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));
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
        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
}