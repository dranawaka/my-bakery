package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.model.Payment;
import com.aurelius.tech.mybakery.model.Payment.PaymentMethod;
import com.aurelius.tech.mybakery.model.Payment.PaymentStatus;
import com.aurelius.tech.mybakery.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling payment endpoints.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Constructor with dependencies.
     */
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Create a new payment for an order.
     *
     * @param requestBody the request body containing order ID and payment method
     * @return a response entity with the created payment
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<?>> createPayment(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract order ID and payment method from request body
            Long orderId = Long.valueOf(requestBody.get("orderId").toString());
            PaymentMethod paymentMethod = PaymentMethod.valueOf(requestBody.get("paymentMethod").toString());
            
            // Create payment
            Payment payment = paymentService.createPayment(orderId, paymentMethod);
            
            return ResponseEntity.ok(ApiResponse.success(payment, "Payment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Process a payment.
     *
     * @param paymentId the payment ID
     * @param requestBody the request body containing payment details
     * @return a response entity with the processed payment
     */
    @PostMapping("/{paymentId}/process")
    public ResponseEntity<ApiResponse<?>> processPayment(
            @PathVariable Long paymentId,
            @RequestBody Map<String, String> requestBody) {
        try {
            // Process payment
            Payment payment = paymentService.processPayment(paymentId, requestBody);
            
            return ResponseEntity.ok(ApiResponse.success(payment, "Payment processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a payment by ID.
     *
     * @param id the payment ID
     * @return a response entity with the payment
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(ApiResponse.success(payment, "Payment retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get payments by order ID.
     *
     * @param orderId the order ID
     * @return a response entity with the payments
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<?>> getPaymentsByOrderId(@PathVariable Long orderId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
            return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Process a refund.
     *
     * @param paymentId the payment ID
     * @param requestBody the request body containing refund amount and reason
     * @return a response entity with the refunded payment
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<?>> processRefund(
            @PathVariable Long paymentId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            // Extract refund amount and reason from request body
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            String reason = requestBody.get("reason").toString();
            
            // Process refund
            Payment payment = paymentService.processRefund(paymentId, amount, reason);
            
            return ResponseEntity.ok(ApiResponse.success(payment, "Refund processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cancel a payment.
     *
     * @param paymentId the payment ID
     * @return a response entity with the cancelled payment
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.cancelPayment(paymentId);
            return ResponseEntity.ok(ApiResponse.success(payment, "Payment cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get payment status.
     *
     * @param paymentId the payment ID
     * @return a response entity with the payment status
     */
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<?>> getPaymentStatus(@PathVariable Long paymentId) {
        try {
            PaymentStatus status = paymentService.getPaymentStatus(paymentId);
            return ResponseEntity.ok(ApiResponse.success(status, "Payment status retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Check if an order has been paid.
     *
     * @param orderId the order ID
     * @return a response entity with the payment status
     */
    @GetMapping("/order/{orderId}/paid")
    public ResponseEntity<ApiResponse<?>> isOrderPaid(@PathVariable Long orderId) {
        try {
            boolean isPaid = paymentService.isOrderPaid(orderId);
            return ResponseEntity.ok(ApiResponse.success(isPaid, "Order payment status retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}