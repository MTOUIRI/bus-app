package com.booking.platform.repository;

import com.booking.platform.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository {
    
    List findByTripId(Long tripId);
    
    List findByTripIdAndIsAvailableTrue(Long tripId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.trip.id = :tripId AND s.id IN :seatIds AND s.isAvailable = true")
    List findAndLockSeats(@Param("tripId") Long tripId, @Param("seatIds") List seatIds);
    
    @Modifying
    @Query("UPDATE Seat s SET s.isAvailable = :available WHERE s.id IN :seatIds")
    void updateSeatAvailability(@Param("seatIds") List seatIds, @Param("available") Boolean available);
    
    Integer countByTripIdAndIsAvailableTrue(Long tripId);
}