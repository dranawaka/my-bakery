package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Order;
import com.aurelius.tech.mybakery.model.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 * Provides methods for CRUD operations on Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Save an order.
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);
    
    /**
     * Find an order by ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<Order> findById(Long id);
    
    /**
     * Find an order by order number.
     *
     * @param orderNumber the order number to search for
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find all orders.
     *
     * @return a list of all orders
     */
    List<Order> findAll();
    
    /**
     * Delete an order.
     *
     * @param order the order to delete
     */
    void delete(Order order);
    
    /**
     * Delete an order by ID.
     *
     * @param id the ID of the order to delete
     */
    void deleteById(Long id);
    
    /**
     * Find orders by customer ID.
     *
     * @param customerId the customer ID to search for
     * @return a list of orders with the given customer ID
     */
    List<Order> findByCustomer_Id(Long customerId);
    
    /**
     * Find orders by status.
     *
     * @param status the status to search for
     * @return a list of orders with the given status
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Find orders by order date between the given dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of orders with order dates between the given dates
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find pending orders.
     *
     * @return a list of pending orders
     */
    List<Order> findByStatusOrderByOrderDateAsc(OrderStatus status);
    
    /**
     * Find orders by status in the given list of statuses.
     *
     * @param statuses the list of statuses to search for
     * @return a list of orders with statuses in the given list
     */
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    
    /**
     * Count orders by status.
     *
     * @param status the status to count
     * @return the number of orders with the given status
     */
    long countByStatus(OrderStatus status);
}