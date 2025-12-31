package com.booking.platform.entity;

import com.booking.platform.enums.BusType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "buses", indexes = {
    @Index(name = "idx_bus_number", columnList = "busNumber")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 50)
    private String busNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType busType;
    
    @Column(nullable = false)
    private Integer totalSeats;
    
    @Column(nullable = false, length = 100)
    private String manufacturer;
    
    @Column(nullable = false, length = 100)
    private String entity;
    
    @Column(length = 4)
    private String year;
    
    @Column(nullable = false)
    private Boolean hasWifi = false;
    
    @Column(nullable = false)
    private Boolean hasAc = false;
    
    @Column(nullable = false)
    private Boolean hasRestroom = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;
    
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List trips;
}