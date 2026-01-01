package com.booking.platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private BigDecimal totalPrice;
    private String status;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private TripResponse trip;
    private List seats;
}