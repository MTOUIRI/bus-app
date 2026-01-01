package com.booking.platform.dto.request;

import com.booking.platform.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}