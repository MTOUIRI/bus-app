package com.booking.platform.repository;

import com.booking.platform.entity.Bus;
import com.booking.platform.enums.BusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository {
    
    Optional findByBusNumber(String busNumber);
    
    List findByOperatorId(Long operatorId);
    
    List findByBusType(BusType busType);
    
    List findByOperatorIdAndBusType(Long operatorId, BusType busType);
}