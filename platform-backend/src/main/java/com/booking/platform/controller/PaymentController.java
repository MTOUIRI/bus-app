package com.booking.platform.controller;

import com.booking.platform.dto.request.PaymentRequest;
import com.booking.platform.dto.response.ApiResponse;
import com.booking.platform.dto.response.PaymentResponse;
import com.booking.platform.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    @Operation(summary = "Process a payment for a booking")
    public ResponseEntity<ApiResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Processing payment for booking: {}", request.getBookingId());
        PaymentResponse payment = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment));
    }
}