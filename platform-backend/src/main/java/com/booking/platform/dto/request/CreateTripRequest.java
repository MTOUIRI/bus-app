package com.booking.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateTripRequest {
    
    @NotNull(message = "Route ID is required")
    private Long routeId;
    
    @NotNull(message = "Bus ID is required")
    private Long busId;
    
    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;
    
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePrice;
}