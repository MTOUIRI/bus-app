package com.booking.platform.entity;

import com.booking.platform.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_trip_seat_number", columnNames = {"trip_id", "seatNumber"})
    },
    indexes = {
        @Index(name = "idx_trip_available", columnList = "trip_id, isAvailable")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @Column(nullable = false, length = 10)
    private String seatNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    @Column(nullable = false)
    private Boolean isWindow = false;
}