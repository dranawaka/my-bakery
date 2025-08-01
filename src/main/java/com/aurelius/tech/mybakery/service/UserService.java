package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Address;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.AddressRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling user-related operations.
 * This is a simplified version without Spring Security dependencies.
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    
    /**
     * Constructor with dependencies.
     */
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
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
     * Get all users.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role.
     *
     * @param role the role to search for
     * @return a list of users with the given role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Get active users.
     *
     * @return a list of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }
    
    /**
     * Update a user's profile.
     *
     * @param id the user's ID
     * @param updatedUser the updated user information
     * @return the updated user
     * @throws RuntimeException if the user is not found
     */
    public User updateUserProfile(Long id, User updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User existingUser = userOptional.get();
        
        // Update only the fields that can be changed by the user
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Update a user's role.
     *
     * @param id the user's ID
     * @param role the new role
     * @return the updated user
     * @throws RuntimeException if the user is not found
     */
    public User updateUserRole(Long id, User.Role role) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Activate or deactivate a user.
     *
     * @param id the user's ID
     * @param active whether the user should be active
     * @return the updated user
     * @throws RuntimeException if the user is not found
     */
    public User setUserActive(Long id, boolean active) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Delete a user.
     *
     * @param id the user's ID
     * @throws RuntimeException if the user is not found
     */
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        userRepository.deleteById(id);
    }
    
    /**
     * Add an address to a user.
     *
     * @param userId the user's ID
     * @param address the address to add
     * @return the added address
     * @throws RuntimeException if the user is not found
     */
    public Address addUserAddress(Long userId, Address address) {
        // Get the user
        User user = getUserById(userId);
        
        // Set the user and timestamps
        address.setUser(user);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        
        // If this is set as default address, unset any existing default
        if (address.isDefault()) {
            addressRepository.findByUserIdAndIsDefault(userId, true)
                .ifPresent(defaultAddress -> {
                    defaultAddress.setDefault(false);
                    addressRepository.save(defaultAddress);
                });
        }
        
        // Save the address
        return addressRepository.save(address);
    }
    
    /**
     * Get a user's addresses.
     *
     * @param userId the user's ID
     * @return a list of the user's addresses
     * @throws RuntimeException if the user is not found
     */
    public List<Address> getUserAddresses(Long userId) {
        // Verify the user exists
        getUserById(userId);
        
        // Return the user's addresses
        return addressRepository.findByUserId(userId);
    }
    
    /**
     * Update a user's address.
     *
     * @param userId the user's ID
     * @param addressId the address ID
     * @param updatedAddress the updated address information
     * @return the updated address
     * @throws RuntimeException if the address is not found or doesn't belong to the user
     */
    public Address updateUserAddress(Long userId, Long addressId, Address updatedAddress) {
        // Find the address for this user
        Address existingAddress = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new RuntimeException("Address not found or doesn't belong to the user"));
        
        // Update the address fields
        if (updatedAddress.getAddressLine1() != null) {
            existingAddress.setAddressLine1(updatedAddress.getAddressLine1());
        }
        
        if (updatedAddress.getAddressLine2() != null) {
            existingAddress.setAddressLine2(updatedAddress.getAddressLine2());
        }
        
        if (updatedAddress.getCity() != null) {
            existingAddress.setCity(updatedAddress.getCity());
        }
        
        if (updatedAddress.getState() != null) {
            existingAddress.setState(updatedAddress.getState());
        }
        
        if (updatedAddress.getPostalCode() != null) {
            existingAddress.setPostalCode(updatedAddress.getPostalCode());
        }
        
        if (updatedAddress.getCountry() != null) {
            existingAddress.setCountry(updatedAddress.getCountry());
        }
        
        if (updatedAddress.getPhone() != null) {
            existingAddress.setPhone(updatedAddress.getPhone());
        }
        
        if (updatedAddress.getType() != null) {
            existingAddress.setType(updatedAddress.getType());
        }
        
        // Handle default address status
        if (updatedAddress.isDefault() && !existingAddress.isDefault()) {
            // Unset any existing default address
            addressRepository.findByUserIdAndIsDefault(userId, true)
                .ifPresent(defaultAddress -> {
                    defaultAddress.setDefault(false);
                    addressRepository.save(defaultAddress);
                });
            
            existingAddress.setDefault(true);
        }
        
        existingAddress.setUpdatedAt(LocalDateTime.now());
        
        // Save and return the updated address
        return addressRepository.save(existingAddress);
    }
    
    /**
     * Delete a user's address.
     *
     * @param userId the user's ID
     * @param addressId the address ID
     * @throws RuntimeException if the address is not found or doesn't belong to the user
     */
    public void deleteUserAddress(Long userId, Long addressId) {
        // Find the address for this user
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new RuntimeException("Address not found or doesn't belong to the user"));
        
        // Delete the address
        addressRepository.delete(address);
    }
}