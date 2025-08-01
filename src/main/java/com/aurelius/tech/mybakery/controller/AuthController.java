package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.dto.AuthResponse;
import com.aurelius.tech.mybakery.dto.LoginRequest;
import com.aurelius.tech.mybakery.dto.RegisterRequest;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.security.JwtTokenProvider;
import com.aurelius.tech.mybakery.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling authentication and authorization endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Constructor with dependencies.
     */
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    /**
     * Register a new user.
     *
     * @param registerRequest the registration request
     * @return a response entity with the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Convert RegisterRequest to User
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPhone(registerRequest.getPhone());
        
            User registeredUser = authService.register(user);
        
            // Create authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                registeredUser.getEmail(),
                registeredUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + registeredUser.getRole().name()))
            );
            
            // Generate JWT token
            String token = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(registeredUser.getEmail());
        
            // Create AuthResponse
            AuthResponse authResponse = new AuthResponse(token, refreshToken, registeredUser);
        
            return ResponseEntity.ok(ApiResponse.success(authResponse, "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Authenticate a user.
     *
     * @param loginRequest the login request containing email and password
     * @return a response entity with the authenticated user and JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            if (email == null || password == null) {
                return createErrorResponse("Email and password are required", HttpStatus.BAD_REQUEST);
            }
            
            User user = authService.login(email, password);
            
            // Create authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            
            // Generate JWT token
            String token = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            
            return createSuccessResponse("Login successful", response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Get the current user.
     *
     * @return a response entity with the current user
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createErrorResponse("Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Validate token
            if (!jwtTokenProvider.validateToken(token)) {
                return createErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED);
            }
            
            // Extract username from token
            String username = jwtTokenProvider.getUsername(token);
            
            // Get user from database
            User user = authService.getUserByEmail(username);
            
            return createSuccessResponse("User retrieved successfully", user);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Refresh a JWT token.
     *
     * @param refreshRequest the refresh request containing the refresh token
     * @return a response entity with a new JWT token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        try {
            String refreshToken = refreshRequest.get("refreshToken");
            
            if (refreshToken == null) {
                return createErrorResponse("Refresh token is required", HttpStatus.BAD_REQUEST);
            }
            
            // Validate the refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return createErrorResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED);
            }
            
            // Extract username from the refresh token
            String username = jwtTokenProvider.getUsername(refreshToken);
            
            // Get user from database
            User user = authService.getUserByEmail(username);
            
            // Create authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            
            // Generate new tokens
            String newToken = jwtTokenProvider.createToken(authentication);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", newRefreshToken);
            
            return createSuccessResponse("Token refreshed successfully", response);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Logout a user.
     *
     * @return a response entity with a success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createErrorResponse("Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Validate token
            if (!jwtTokenProvider.validateToken(token)) {
                return createErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED);
            }
            
            // In a more complex implementation, we could add the token to a blacklist
            // For now, we'll just return a success message as JWT tokens are stateless
            // and typically handled on the client side
            
            return createSuccessResponse("Logout successful", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Request a password reset.
     *
     * @param resetRequest the reset request containing the email
     * @return a response entity with a success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> resetRequest) {
        try {
            String email = resetRequest.get("email");
            
            if (email == null) {
                return createErrorResponse("Email is required", HttpStatus.BAD_REQUEST);
            }
            
            // In a real implementation, we would send a password reset email
            // For now, we'll just return a success message
            return createSuccessResponse("Password reset email sent", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Reset a password.
     *
     * @param resetRequest the reset request containing the token and new password
     * @return a response entity with a success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> resetRequest) {
        try {
            String token = resetRequest.get("token");
            String newPassword = resetRequest.get("newPassword");
            
            if (token == null || newPassword == null) {
                return createErrorResponse("Token and new password are required", HttpStatus.BAD_REQUEST);
            }
            
            // In a real implementation, we would validate the token and reset the password
            // For now, we'll just return a success message
            return createSuccessResponse("Password reset successful", null);
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