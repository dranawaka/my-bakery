package com.aurelius.tech.mybakery.service;

import com.aurelius.tech.mybakery.model.Invoice;
import com.aurelius.tech.mybakery.model.Invoice.InvoiceStatus;
import com.aurelius.tech.mybakery.model.Order;
import com.aurelius.tech.mybakery.model.Payment;
import com.aurelius.tech.mybakery.model.Payment.PaymentMethod;
import com.aurelius.tech.mybakery.model.Payment.PaymentStatus;
import com.aurelius.tech.mybakery.repository.InvoiceRepository;
import com.aurelius.tech.mybakery.repository.OrderRepository;
import com.aurelius.tech.mybakery.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling payment operations.
 */
@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }
    
    /**
     * Get a payment by ID.
     *
     * @param id the payment ID
     * @return the payment
     * @throws RuntimeException if the payment is not found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    /**
     * Get a payment by payment reference.
     *
     * @param paymentReference the payment reference
     * @return the payment
     * @throws RuntimeException if the payment is not found
     */
    public Payment getPaymentByReference(String paymentReference) {
        return paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    /**
     * Get payments by order ID.
     *
     * @param orderId the order ID
     * @return a list of payments for the order
     */
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    /**
     * Create a new payment for an order.
     *
     * @param orderId the order ID
     * @param paymentMethod the payment method
     * @return the created payment
     * @throws RuntimeException if the order is not found
     */
    @Transactional
    public Payment createPayment(Long orderId, PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if order already has a completed payment
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.COMPLETED)) {
            throw new RuntimeException("Order already has a completed payment");
        }
        
        Payment payment = new Payment(order, order.getTotalAmount(), paymentMethod);
        return paymentRepository.save(payment);
    }
    
    /**
     * Process a payment.
     *
     * @param paymentId the payment ID
     * @param paymentDetails the payment details (card number, expiry date, etc.)
     * @return the processed payment
     * @throws RuntimeException if the payment is not found or cannot be processed
     */
    @Transactional
    public Payment processPayment(Long paymentId, Map<String, String> paymentDetails) {
        Payment payment = getPaymentById(paymentId);
        
        // Check if payment can be processed
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment cannot be processed");
        }
        
        // Update payment status
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);
        
        // Simulate payment gateway integration
        Map<String, Object> gatewayResponse = processPaymentWithGateway(payment, paymentDetails);
        
        // Update payment with gateway response
        payment.setTransactionId((String) gatewayResponse.get("transactionId"));
        payment.setGatewayResponse(gatewayResponse.toString());
        
        // Update payment status based on gateway response
        boolean success = (boolean) gatewayResponse.get("success");
        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            
            // Update order status
            Order order = payment.getOrder();
            order.setPaymentStatus("PAID");
            orderRepository.save(order);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Process a refund.
     *
     * @param paymentId the payment ID
     * @param amount the refund amount
     * @param reason the refund reason
     * @return the refunded payment
     * @throws RuntimeException if the payment is not found or cannot be refunded
     */
    @Transactional
    public Payment processRefund(Long paymentId, BigDecimal amount, String reason) {
        Payment payment = getPaymentById(paymentId);
        
        // Check if payment can be refunded
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment cannot be refunded");
        }
        
        // Check if refund amount is valid
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Invalid refund amount");
        }
        
        // Simulate refund with payment gateway
        Map<String, Object> refundResponse = processRefundWithGateway(payment, amount, reason);
        
        // Update payment with refund response
        String refundInfo = "Refund: " + amount + ", Reason: " + reason + ", Response: " + refundResponse;
        payment.setGatewayResponse(payment.getGatewayResponse() + "; " + refundInfo);
        
        // Update payment status based on refund amount
        if (amount.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
            
            // Update order status directly
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.REFUNDED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Cancel a payment.
     *
     * @param paymentId the payment ID
     * @return the cancelled payment
     * @throws RuntimeException if the payment is not found or cannot be cancelled
     */
    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        // Check if payment can be cancelled
        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new RuntimeException("Payment cannot be cancelled");
        }
        
        // Update payment status
        payment.setStatus(PaymentStatus.CANCELLED);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Get payment status.
     *
     * @param paymentId the payment ID
     * @return the payment status
     * @throws RuntimeException if the payment is not found
     */
    public PaymentStatus getPaymentStatus(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        return payment.getStatus();
    }
    
    /**
     * Check if an order has been paid.
     *
     * @param orderId the order ID
     * @return true if the order has been paid, false otherwise
     */
    public boolean isOrderPaid(Long orderId) {
        return paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.COMPLETED);
    }
    
    /**
     * Simulate payment gateway integration.
     *
     * @param payment the payment to process
     * @param paymentDetails the payment details
     * @return the gateway response
     */
    private Map<String, Object> processPaymentWithGateway(Payment payment, Map<String, String> paymentDetails) {
        // This is a simulated payment gateway integration
        // In a real implementation, this would call an actual payment gateway API
        
        Map<String, Object> response = new HashMap<>();
        
        // Generate a transaction ID
        String transactionId = UUID.randomUUID().toString();
        response.put("transactionId", transactionId);
        
        // Simulate payment processing
        boolean success = Math.random() > 0.1; // 90% success rate
        response.put("success", success);
        
        if (success) {
            response.put("message", "Payment processed successfully");
            response.put("authorizationCode", "AUTH" + (int) (Math.random() * 1000000));
        } else {
            response.put("message", "Payment processing failed");
            response.put("errorCode", "ERR" + (int) (Math.random() * 1000));
        }
        
        return response;
    }
    
    /**
     * Simulate refund with payment gateway.
     *
     * @param payment the payment to refund
     * @param amount the refund amount
     * @param reason the refund reason
     * @return the refund response
     */
    private Map<String, Object> processRefundWithGateway(Payment payment, BigDecimal amount, String reason) {
        // This is a simulated refund with payment gateway
        // In a real implementation, this would call an actual payment gateway API
        
        Map<String, Object> response = new HashMap<>();
        
        // Generate a refund ID
        String refundId = "REF-" + UUID.randomUUID().toString().substring(0, 8);
        response.put("refundId", refundId);
        
        // Simulate refund processing
        boolean success = Math.random() > 0.05; // 95% success rate
        response.put("success", success);
        
        if (success) {
            response.put("message", "Refund processed successfully");
            response.put("refundAmount", amount);
        } else {
            response.put("message", "Refund processing failed");
            response.put("errorCode", "ERR" + (int) (Math.random() * 1000));
        }
        
        return response;
    }
}