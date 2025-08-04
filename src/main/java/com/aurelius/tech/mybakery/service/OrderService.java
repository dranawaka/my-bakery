package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Order;
import com.aurelius.tech.mybakery.model.Order.OrderStatus;
import com.aurelius.tech.mybakery.model.OrderItem;
import com.aurelius.tech.mybakery.model.Product;
import com.aurelius.tech.mybakery.model.User;
import com.aurelius.tech.mybakery.repository.OrderRepository;
import com.aurelius.tech.mybakery.repository.ProductRepository;
import com.aurelius.tech.mybakery.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling order-related operations.
 */
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    
    /**
     * Constructor with dependencies.
     */
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                       ProductRepository productRepository, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }
    
    /**
     * Create a new order.
     *
     * @param order the order to create
     * @return the created order
     * @throws RuntimeException if the customer is not found or products are not available
     */
    public Order createOrder(Order order) {
        // Validate customer
        if (order.getCustomerId() != null) {
            Optional<User> customerOptional = userRepository.findById(order.getCustomerId());
            if (customerOptional.isEmpty()) {
                throw new RuntimeException("Customer not found");
            }
            order.setCustomer(customerOptional.get());
        }
        
        // Generate order number
        order.setOrderNumber(generateOrderNumber());
        
        // Set initial status
        order.setStatus(OrderStatus.PENDING);
        
        // Set dates
        LocalDateTime now = LocalDateTime.now();
        order.setOrderDate(now);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        
        // Set delivery method if not already set
        if (order.getDeliveryMethod() == null) {
            order.setDeliveryMethod(Order.DeliveryMethod.DELIVERY); // Default to delivery
        }
        
        // Process order items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            processOrderItems(order);
        }
        
        // Calculate totals
        calculateOrderTotals(order);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Update inventory
        updateInventoryForOrder(savedOrder);
        
        return savedOrder;
    }
    
    /**
     * Get an order by ID.
     *
     * @param id the order's ID
     * @return the order
     * @throws RuntimeException if the order is not found
     */
    public Order getOrderById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        return orderOptional.get();
    }
    
    /**
     * Get an order by order number.
     *
     * @param orderNumber the order number
     * @return the order
     * @throws RuntimeException if the order is not found
     */
    public Order getOrderByOrderNumber(String orderNumber) {
        Optional<Order> orderOptional = orderRepository.findByOrderNumber(orderNumber);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        return orderOptional.get();
    }
    
    /**
     * Get all orders.
     *
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Get orders by customer ID.
     *
     * @param customerId the customer ID
     * @return a list of orders for the given customer
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }
    
    /**
     * Get orders by status.
     *
     * @param status the order status
     * @return a list of orders with the given status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Get pending orders.
     *
     * @return a list of pending orders
     */
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusOrderByOrderDateAsc(OrderStatus.PENDING);
    }
    
    /**
     * Get orders by date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of orders with order dates between the given dates
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    /**
     * Update an order's status.
     *
     * @param id the order's ID
     * @param status the new status
     * @return the updated order
     * @throws RuntimeException if the order is not found
     */
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        Order order = orderOptional.get();
        
        // Check if the status transition is valid
        if (!isValidStatusTransition(order.getStatus(), status)) {
            throw new RuntimeException("Invalid status transition from " + order.getStatus() + " to " + status);
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    /**
     * Cancel an order.
     *
     * @param id the order's ID
     * @return the cancelled order
     * @throws RuntimeException if the order is not found or cannot be cancelled
     */
    public Order cancelOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        Order order = orderOptional.get();
        
        // Check if the order can be cancelled
        if (!canCancelOrder(order)) {
            throw new RuntimeException("Order cannot be cancelled");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order cancelledOrder = orderRepository.save(order);
        
        // Restore inventory
        restoreInventoryForOrder(cancelledOrder);
        
        return cancelledOrder;
    }
    
    /**
     * Delete an order.
     *
     * @param id the order's ID
     * @throws RuntimeException if the order is not found
     */
    public void deleteOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        orderRepository.deleteById(id);
    }
    
    /**
     * Generate a unique order number.
     *
     * @return a unique order number
     */
    private String generateOrderNumber() {
        // Generate a random UUID and take the first 8 characters
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Process order items.
     *
     * @param order the order to process
     */
    private void processOrderItems(Order order) {
        List<OrderItem> processedItems = new ArrayList<>();
        
        for (OrderItem item : order.getItems()) {
            // Validate product
            Optional<Product> productOptional = productRepository.findById(item.getProductId());
            if (productOptional.isEmpty()) {
                throw new RuntimeException("Product not found: " + item.getProductId());
            }
            
            Product product = productOptional.get();
            item.setProduct(product);
            
            // Set unit price if not set
            if (item.getUnitPrice() == null) {
                item.setUnitPrice(product.getPrice());
            }
            
            // Calculate total price
            item.calculateTotalPrice();
            
            processedItems.add(item);
        }
        
        order.setItems(processedItems);
    }
    
    /**
     * Calculate order totals.
     *
     * @param order the order to calculate totals for
     */
    private void calculateOrderTotals(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Sum item totals
        for (OrderItem item : order.getItems()) {
            if (item.getTotalPrice() != null) {
                totalAmount = totalAmount.add(item.getTotalPrice());
            }
        }
        
        // Apply tax if set
        if (order.getTaxAmount() != null) {
            totalAmount = totalAmount.add(order.getTaxAmount());
        }
        
        // Apply shipping if set
        if (order.getShippingAmount() != null) {
            totalAmount = totalAmount.add(order.getShippingAmount());
        }
        
        // Apply discount if set
        if (order.getDiscountAmount() != null) {
            totalAmount = totalAmount.subtract(order.getDiscountAmount());
        }
        
        order.setTotalAmount(totalAmount);
    }
    
    /**
     * Update inventory for an order.
     *
     * @param order the order to update inventory for
     */
    private void updateInventoryForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            try {
                // Check if inventory exists, if not create it with default quantity
                if (!inventoryService.isInStock(item.getProductId(), item.getQuantity())) {
                    // Create inventory record with default quantity if it doesn't exist
                    inventoryService.increaseInventory(item.getProductId(), 100); // Default stock
                }
                inventoryService.decreaseInventory(item.getProductId(), item.getQuantity());
            } catch (RuntimeException e) {
                // Log the error but continue processing
                System.err.println("Error updating inventory: " + e.getMessage());
            }
        }
    }
    
    /**
     * Restore inventory for a cancelled order.
     *
     * @param order the cancelled order to restore inventory for
     */
    private void restoreInventoryForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            try {
                inventoryService.increaseInventory(item.getProductId(), item.getQuantity());
            } catch (RuntimeException e) {
                // Log the error but continue processing
                System.err.println("Error restoring inventory: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if a status transition is valid.
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Terminal states cannot be changed
        if (currentStatus == OrderStatus.COMPLETED || 
            currentStatus == OrderStatus.CANCELLED || 
            currentStatus == OrderStatus.REFUNDED) {
            return false;
        }
        
        // Normal flow: PENDING → CONFIRMED → PREPARING → READY → COMPLETED
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING:
                return newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY:
                return newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED;
            default:
                return false;
        }
    }
    
    /**
     * Check if an order can be cancelled.
     *
     * @param order the order to check
     * @return true if the order can be cancelled, false otherwise
     */
    private boolean canCancelOrder(Order order) {
        // Terminal states cannot be cancelled
        return order.getStatus() != OrderStatus.COMPLETED && 
               order.getStatus() != OrderStatus.CANCELLED && 
               order.getStatus() != OrderStatus.REFUNDED;
    }
    
    /**
     * Get recent orders (last 10 orders).
     *
     * @return a list of recent orders
     */
    public List<Order> getRecentOrders() {
        // Get all orders sorted by order date (descending) and limit to 10
        return orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(10)
                .toList();
    }
}