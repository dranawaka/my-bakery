package com.aurelius.tech.mybakery.repository;

import com.aurelius.tech.mybakery.model.Invoice;
import com.aurelius.tech.mybakery.model.Invoice.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Invoice entity.
 * Provides methods for CRUD operations on Invoice entities.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * Find an invoice by invoice number.
     *
     * @param invoiceNumber the invoice number to search for
     * @return an Optional containing the invoice if found, or empty if not found
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    /**
     * Find invoices by order ID.
     *
     * @param orderId the order ID to search for
     * @return a list of invoices with the given order ID
     */
    List<Invoice> findByOrderId(Long orderId);
    
    /**
     * Find invoices by payment ID.
     *
     * @param paymentId the payment ID to search for
     * @return a list of invoices with the given payment ID
     */
    List<Invoice> findByPaymentId(Long paymentId);
    
    /**
     * Find invoices by status.
     *
     * @param status the status to search for
     * @return a list of invoices with the given status
     */
    List<Invoice> findByStatus(InvoiceStatus status);
    
    /**
     * Find invoices by invoice date between the given dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of invoices with invoice dates between the given dates
     */
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find invoices by due date before the given date.
     *
     * @param dueDate the due date to compare with
     * @return a list of invoices with due dates before the given date
     */
    List<Invoice> findByDueDateBefore(LocalDateTime dueDate);
    
    /**
     * Find invoices by order ID and status.
     *
     * @param orderId the order ID to search for
     * @param status the status to search for
     * @return a list of invoices with the given order ID and status
     */
    List<Invoice> findByOrderIdAndStatus(Long orderId, InvoiceStatus status);
    
    /**
     * Find invoices by payment ID and status.
     *
     * @param paymentId the payment ID to search for
     * @param status the status to search for
     * @return a list of invoices with the given payment ID and status
     */
    List<Invoice> findByPaymentIdAndStatus(Long paymentId, InvoiceStatus status);
    
    /**
     * Check if an invoice exists for an order with a paid status.
     *
     * @param orderId the order ID to check
     * @return true if a paid invoice exists for the order, false otherwise
     */
    boolean existsByOrderIdAndStatus(Long orderId, InvoiceStatus status);
    
    /**
     * Count invoices by status.
     *
     * @param status the status to count
     * @return the number of invoices with the given status
     */
    long countByStatus(InvoiceStatus status);
}