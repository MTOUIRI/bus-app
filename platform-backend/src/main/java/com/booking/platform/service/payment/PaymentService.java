package com.booking.platform.service.payment;

import com.booking.platform.entity.Booking;
import com.booking.platform.entity.Payment;
import com.booking.platform.enums.BookingStatus;
import com.booking.platform.enums.PaymentMethod;
import com.booking.platform.enums.PaymentStatus;
import com.booking.platform.dto.request.PaymentRequest;
import com.booking.platform.dto.response.PaymentResponse;
import com.booking.platform.exception.PaymentException;
import com.booking.platform.exception.ResourceNotFoundException;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.PaymentRepository;
import com.booking.platform.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final PaymentGatewayService paymentGatewayService;
    
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for booking: {}", request.getBookingId());
        
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new PaymentException("Only pending bookings can be paid");
        }
        
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PaymentException("Booking has expired");
        }
        
        // Create payment record
        Payment payment = Payment.builder()
            .booking(booking)
            .transactionId(generateTransactionId())
            .amount(booking.getTotalPrice())
            .paymentMethod(request.getPaymentMethod())
            .status(PaymentStatus.PROCESSING)
            .build();
        
        payment = paymentRepository.save(payment);
        
        try {
            // Process payment through gateway
            String gatewayResponse = paymentGatewayService.processPayment(
                payment.getTransactionId(),
                booking.getTotalPrice(),
                request.getPaymentMethod()
            );
            
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayResponse(gatewayResponse);
            
            // Confirm the booking
            bookingService.confirmBooking(booking.getId());
            
            log.info("Payment completed successfully: {}", payment.getTransactionId());
            
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse(e.getMessage());
            log.error("Payment failed: {}", e.getMessage());
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
        
        payment = paymentRepository.save(payment);
        
        return PaymentResponse.builder()
            .transactionId(payment.getTransactionId())
            .amount(payment.getAmount())
            .status(payment.getStatus().name())
            .paymentMethod(payment.getPaymentMethod().name())
            .paidAt(payment.getPaidAt())
            .build();
    }
    
    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}