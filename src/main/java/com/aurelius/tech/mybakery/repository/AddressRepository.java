package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Address entity operations.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Find all addresses for a specific user.
     *
     * @param userId the user's ID
     * @return a list of addresses
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * Find a specific address for a user.
     *
     * @param id the address ID
     * @param userId the user's ID
     * @return an optional containing the address if found
     */
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    
    /**
     * Delete all addresses for a specific user.
     *
     * @param userId the user's ID
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find the default address for a user.
     *
     * @param userId the user's ID
     * @param isDefault whether the address is default
     * @return an optional containing the address if found
     */
    Optional<Address> findByUserIdAndIsDefault(Long userId, boolean isDefault);
}