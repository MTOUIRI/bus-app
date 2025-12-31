package com.booking.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "operators", indexes = {
    @Index(name = "idx_operator_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operator extends BaseEntity {
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column
    private String logoUrl;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List buses;
    
    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List trips;
}