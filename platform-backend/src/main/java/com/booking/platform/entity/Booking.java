package com.booking.platform.entity;

import com.booking.platform.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_code", columnList = "bookingCode"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_trip_id", columnList = "trip_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    
    @Column(nullable = false, unique = true, length = 20)
    private String bookingCode;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    
    @Column(nullable = false, length = 150)
    private String passengerName;
    
    @Column(nullable = false, length = 150)
    private String passengerEmail;
    
    @Column(nullable = false, length = 20)
    private String passengerPhone;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List seats;
    
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
    
    private LocalDateTime expiresAt;
    
    @PrePersist
    public void generateBookingCode() {
        if (bookingCode == null) {
            bookingCode = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}