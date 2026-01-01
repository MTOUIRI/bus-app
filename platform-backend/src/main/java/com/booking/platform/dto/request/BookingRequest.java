package com.booking.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    
    @NotNull(message = "Trip ID is required")
    private Long tripId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List seatIds;
    
    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 150)
    private String passengerName;
    
    @NotBlank(message = "Passenger email is required")
    @Email(message = "Invalid email format")
    private String passengerEmail;
    
    @NotBlank(message = "Passenger phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String passengerPhone;
}