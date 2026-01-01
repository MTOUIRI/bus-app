package com.booking.platform.service.payment;

import com.booking.platform.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Payment Gateway Service - Simulated implementation
 * In production, integrate with real payment gateways like Stripe, PayPal, etc.
 */
@Service
@Slf4j
public class PaymentGatewayService {
    
    public String processPayment(String transactionId, BigDecimal amount, PaymentMethod method) {
        log.info("Processing payment through gateway - Transaction: {}, Amount: {}, Method: {}", 
            transactionId, amount, method);
        
        // Simulate payment gateway processing
        // In production, call actual gateway API
        
        try {
            Thread.sleep(1000); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate successful payment
        return String.format("Payment successful - Gateway Response ID: GW%s", 
            transactionId.substring(3));
    }
}