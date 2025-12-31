package com.booking.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "routes", indexes = {
    @Index(name = "idx_origin_destination", columnList = "origin, destination")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {
    
    @Column(nullable = false, length = 200)
    private String origin;
    
    @Column(nullable = false, length = 200)
    private String destination;
    
    @Column(nullable = false)
    private Integer distanceKm;
    
    @Column(nullable = false)
    private Integer estimatedDurationMinutes;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List trips;
}