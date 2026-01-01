package com.booking.platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime paidAt;
}