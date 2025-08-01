package com.aurelius.tech.mybakery.config;

import com.aurelius.tech.mybakery.model.Category;
import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.CategoryRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Configuration class to initialize data on application startup.
 * This includes creating an admin user, sample categories, and products if they don't exist.
 */
@Configuration
public class DataInitializer {

    @Value("${admin.username:admin@mybakery.com}")
    private String adminUsername;

    @Value("${admin.password:Admin@123}")
    private String adminPassword;

    /**
     * Creates a CommandLineRunner bean that initializes data on application startup.
     *
     * @param userRepository the user repository
     * @param categoryRepository the category repository
     * @param productRepository the product repository
     * @param passwordEncoder the password encoder
     * @return a CommandLineRunner
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                    CategoryRepository categoryRepository,
                                    ProductRepository productRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if it doesn't exist
            Optional<User> adminUser = userRepository.findByEmail(adminUsername);
            if (adminUser.isEmpty()) {
                User admin = new User();
                admin.setEmail(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(User.Role.ADMIN);
                admin.setEmailVerified(true);
                admin.setActive(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                userRepository.save(admin);
                
                System.out.println("Admin user created with username: " + adminUsername);
            }
            
            // Create sample categories if they don't exist
            createSampleCategories(categoryRepository);
            
            // Create sample products if they don't exist
            createSampleProducts(categoryRepository, productRepository);
            
            // Create sample customers if they don't exist
            createSampleCustomers(userRepository, passwordEncoder);
        };
    }
    
    /**
     * Creates sample categories for the bakery.
     */
    private void createSampleCategories(CategoryRepository categoryRepository) {
        // Check if categories already exist
        if (categoryRepository.count() > 0) {
            return;
        }
        
        // Create main categories
        Category breads = new Category();
        breads.setName("Breads");
        breads.setDescription("Fresh baked breads and rolls");
        breads.setActive(true);
        breads.setCreatedAt(LocalDateTime.now());
        breads.setUpdatedAt(LocalDateTime.now());
        breads = categoryRepository.save(breads);
        
        Category pastries = new Category();
        pastries.setName("Pastries");
        pastries.setDescription("Sweet pastries and desserts");
        pastries.setActive(true);
        pastries.setCreatedAt(LocalDateTime.now());
        pastries.setUpdatedAt(LocalDateTime.now());
        pastries = categoryRepository.save(pastries);
        
        Category cakes = new Category();
        cakes.setName("Cakes");
        cakes.setDescription("Birthday cakes and special occasion cakes");
        cakes.setActive(true);
        cakes.setCreatedAt(LocalDateTime.now());
        cakes.setUpdatedAt(LocalDateTime.now());
        cakes = categoryRepository.save(cakes);
        
        Category cookies = new Category();
        cookies.setName("Cookies");
        cookies.setDescription("Fresh baked cookies");
        cookies.setActive(true);
        cookies.setCreatedAt(LocalDateTime.now());
        cookies.setUpdatedAt(LocalDateTime.now());
        cookies = categoryRepository.save(cookies);
        
        System.out.println("Sample categories created successfully");
    }
    
    /**
     * Creates sample products for the bakery.
     */
    private void createSampleProducts(CategoryRepository categoryRepository, ProductRepository productRepository) {
        // Check if products already exist
        if (productRepository.count() > 0) {
            return;
        }
        
        // Get categories
        Category breads = categoryRepository.findByName("Breads").orElse(null);
        Category pastries = categoryRepository.findByName("Pastries").orElse(null);
        Category cakes = categoryRepository.findByName("Cakes").orElse(null);
        Category cookies = categoryRepository.findByName("Cookies").orElse(null);
        
        if (breads != null) {
            // Create bread products
            Product sourdough = new Product();
            sourdough.setName("Sourdough Bread");
            sourdough.setDescription("Traditional sourdough bread with crispy crust");
            sourdough.setPrice(new BigDecimal("4.99"));
            sourdough.setCostPrice(new BigDecimal("2.50"));
            sourdough.setSku("BREAD-001");
            sourdough.setBarcode("1234567890123");
            sourdough.setCategory(breads);
            sourdough.setActive(true);
            sourdough.setFeatured(true);
            sourdough.setCreatedAt(LocalDateTime.now());
            sourdough.setUpdatedAt(LocalDateTime.now());
            productRepository.save(sourdough);
            
            Product wholeWheat = new Product();
            wholeWheat.setName("Whole Wheat Bread");
            wholeWheat.setDescription("Healthy whole wheat bread");
            wholeWheat.setPrice(new BigDecimal("3.99"));
            wholeWheat.setCostPrice(new BigDecimal("2.00"));
            wholeWheat.setSku("BREAD-002");
            wholeWheat.setBarcode("1234567890124");
            wholeWheat.setCategory(breads);
            wholeWheat.setActive(true);
            wholeWheat.setFeatured(false);
            wholeWheat.setCreatedAt(LocalDateTime.now());
            wholeWheat.setUpdatedAt(LocalDateTime.now());
            productRepository.save(wholeWheat);
        }
        
        if (pastries != null) {
            // Create pastry products
            Product croissant = new Product();
            croissant.setName("Butter Croissant");
            croissant.setDescription("Flaky butter croissant");
            croissant.setPrice(new BigDecimal("2.99"));
            croissant.setCostPrice(new BigDecimal("1.50"));
            croissant.setSku("PASTRY-001");
            croissant.setBarcode("1234567890125");
            croissant.setCategory(pastries);
            croissant.setActive(true);
            croissant.setFeatured(true);
            croissant.setCreatedAt(LocalDateTime.now());
            croissant.setUpdatedAt(LocalDateTime.now());
            productRepository.save(croissant);
        }
        
        if (cakes != null) {
            // Create cake products
            Product chocolateCake = new Product();
            chocolateCake.setName("Chocolate Cake");
            chocolateCake.setDescription("Rich chocolate cake with chocolate frosting");
            chocolateCake.setPrice(new BigDecimal("25.99"));
            chocolateCake.setCostPrice(new BigDecimal("12.00"));
            chocolateCake.setSku("CAKE-001");
            chocolateCake.setBarcode("1234567890126");
            chocolateCake.setCategory(cakes);
            chocolateCake.setActive(true);
            chocolateCake.setFeatured(true);
            chocolateCake.setCreatedAt(LocalDateTime.now());
            chocolateCake.setUpdatedAt(LocalDateTime.now());
            productRepository.save(chocolateCake);
        }
        
        if (cookies != null) {
            // Create cookie products
            Product chocolateChip = new Product();
            chocolateChip.setName("Chocolate Chip Cookies");
            chocolateChip.setDescription("Classic chocolate chip cookies");
            chocolateChip.setPrice(new BigDecimal("1.99"));
            chocolateChip.setCostPrice(new BigDecimal("0.75"));
            chocolateChip.setSku("COOKIE-001");
            chocolateChip.setBarcode("1234567890127");
            chocolateChip.setCategory(cookies);
            chocolateChip.setActive(true);
            chocolateChip.setFeatured(false);
            chocolateChip.setCreatedAt(LocalDateTime.now());
            chocolateChip.setUpdatedAt(LocalDateTime.now());
            productRepository.save(chocolateChip);
        }
        
        System.out.println("Sample products created successfully");
    }
    
    /**
     * Creates sample customers for the bakery.
     */
    private void createSampleCustomers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Check if customers already exist
        if (userRepository.count() > 1) { // More than just the admin user
            return;
        }
        
        // Create sample customers
        User customer1 = new User();
        customer1.setEmail("john.doe@example.com");
        customer1.setPassword(passwordEncoder.encode("password123"));
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setPhone("555-0101");
        customer1.setRole(User.Role.CUSTOMER);
        customer1.setEmailVerified(true);
        customer1.setActive(true);
        customer1.setCreatedAt(LocalDateTime.now());
        customer1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(customer1);
        
        User customer2 = new User();
        customer2.setEmail("jane.smith@example.com");
        customer2.setPassword(passwordEncoder.encode("password123"));
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setPhone("555-0102");
        customer2.setRole(User.Role.CUSTOMER);
        customer2.setEmailVerified(true);
        customer2.setActive(true);
        customer2.setCreatedAt(LocalDateTime.now());
        customer2.setUpdatedAt(LocalDateTime.now());
        userRepository.save(customer2);
        
        User staff1 = new User();
        staff1.setEmail("bob.baker@mybakery.com");
        staff1.setPassword(passwordEncoder.encode("password123"));
        staff1.setFirstName("Bob");
        staff1.setLastName("Baker");
        staff1.setPhone("555-0201");
        staff1.setRole(User.Role.STAFF);
        staff1.setEmailVerified(true);
        staff1.setActive(true);
        staff1.setCreatedAt(LocalDateTime.now());
        staff1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(staff1);
        
        User manager1 = new User();
        manager1.setEmail("sarah.manager@mybakery.com");
        manager1.setPassword(passwordEncoder.encode("password123"));
        manager1.setFirstName("Sarah");
        manager1.setLastName("Manager");
        manager1.setPhone("555-0301");
        manager1.setRole(User.Role.MANAGER);
        manager1.setEmailVerified(true);
        manager1.setActive(true);
        manager1.setCreatedAt(LocalDateTime.now());
        manager1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(manager1);
        
        System.out.println("Sample customers and staff created successfully");
    }
}