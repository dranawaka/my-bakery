package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.model.Order;
import com.aurelius.tech.mybakery.model.Order.OrderStatus;
import com.aurelius.tech.mybakery.service.OrderService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling order-related endpoints.
 * This is a simplified version without Spring Security dependencies.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Constructor with dependencies.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Create a new order.
     *
     * @param order the order to create
     * @return a response entity with the created order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return createSuccessResponse("Order created successfully", createdOrder);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all orders.
     *
     * @return a response entity with all orders
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return createSuccessResponse("Orders retrieved successfully", orders);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get an order by ID.
     *
     * @param id the order ID
     * @return a response entity with the order
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return createSuccessResponse("Order retrieved successfully", order);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Get an order by order number.
     *
     * @param orderNumber the order number
     * @return a response entity with the order
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber);
            return createSuccessResponse("Order retrieved successfully", order);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Update an order's status.
     *
     * @param id the order ID
     * @param status the new status
     * @return a response entity with the updated order
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return createSuccessResponse("Order status updated successfully", updatedOrder);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Cancel an order.
     *
     * @param id the order ID
     * @return a response entity with the cancelled order
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            Order cancelledOrder = orderService.cancelOrder(id);
            return createSuccessResponse("Order cancelled successfully", cancelledOrder);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete an order.
     *
     * @param id the order ID
     * @return a response entity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return createSuccessResponse("Order deleted successfully", null);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get orders by customer ID.
     *
     * @param customerId the customer ID
     * @return a response entity with the matching orders
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable Long customerId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            return createSuccessResponse("Orders retrieved successfully", orders);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get orders by status.
     *
     * @param status the order status
     * @return a response entity with the matching orders
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable OrderStatus status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            return createSuccessResponse("Orders retrieved successfully", orders);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get pending orders.
     *
     * @return a response entity with the pending orders
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrders() {
        try {
            List<Order> orders = orderService.getPendingOrders();
            return createSuccessResponse("Orders retrieved successfully", orders);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get orders by date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a response entity with the matching orders
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
            return createSuccessResponse("Orders retrieved successfully", orders);
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