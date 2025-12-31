package com.booking.platform.entity;

import com.booking.platform.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_route_date", columnList = "route_id, departureTime"),
    @Index(name = "idx_trip_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;
    
    @Column(nullable = false)
    private LocalDateTime departureTime;
    
    @Column(nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;
    
    @Column(nullable = false)
    private Integer availableSeats;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List seats;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List bookings;
    
    @PrePersist
    @PreUpdate
    public void calculateAvailableSeats() {
        if (seats != null) {
            this.availableSeats = (int) seats.stream()
                .filter(Seat::getIsAvailable)
                .count();
        }
    }
}