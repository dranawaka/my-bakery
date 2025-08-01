package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides methods for CRUD operations on User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Save a user.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
    
    /**
     * Find a user by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(Long id);
    
    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users.
     *
     * @return a list of all users
     */
    List<User> findAll();
    
    /**
     * Delete a user.
     *
     * @param user the user to delete
     */
    void delete(User user);
    
    /**
     * Delete a user by ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteById(Long id);
    
    /**
     * Check if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user exists with the given email, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by role.
     *
     * @param role the role to search for
     * @return a list of users with the given role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Find active users.
     *
     * @return a list of active users
     */
    List<User> findByActiveTrue();
}