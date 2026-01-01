package com.booking.platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TripResponse {
    private Long id;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private String status;
    private Integer availableSeats;
    private Integer totalSeats;
    private String operatorName;
    private String busNumber;
    private String busType;
    private Boolean hasWifi;
    private Boolean hasAc;
    private Boolean hasRestroom;
}