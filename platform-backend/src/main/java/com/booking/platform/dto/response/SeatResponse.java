package com.booking.platform.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private String seatType;
    private Boolean isAvailable;
    private Boolean isWindow;
}