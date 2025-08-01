package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Payment;
import com.aurelius.tech.mybakery.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity.
 * Provides methods for CRUD operations on Payment entities.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payments by order ID.
     *
     * @param orderId the order ID to search for
     * @return a list of payments with the given order ID
     */
    List<Payment> findByOrderId(Long orderId);
    
    /**
     * Find a payment by payment reference.
     *
     * @param paymentReference the payment reference to search for
     * @return an Optional containing the payment if found, or empty if not found
     */
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    /**
     * Find a payment by transaction ID.
     *
     * @param transactionId the transaction ID to search for
     * @return an Optional containing the payment if found, or empty if not found
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payments by status.
     *
     * @param status the status to search for
     * @return a list of payments with the given status
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Find payments by payment method.
     *
     * @param paymentMethod the payment method to search for
     * @return a list of payments with the given payment method
     */
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    /**
     * Find payments by payment date between the given dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of payments with payment dates between the given dates
     */
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find payments by order ID and status.
     *
     * @param orderId the order ID to search for
     * @param status the status to search for
     * @return a list of payments with the given order ID and status
     */
    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
    
    /**
     * Count payments by status.
     *
     * @param status the status to count
     * @return the number of payments with the given status
     */
    long countByStatus(PaymentStatus status);
    
    /**
     * Check if a payment exists for an order with a completed status.
     *
     * @param orderId the order ID to check
     * @return true if a completed payment exists for the order, false otherwise
     */
    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
}