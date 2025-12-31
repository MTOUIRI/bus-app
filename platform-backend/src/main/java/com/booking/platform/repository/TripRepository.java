package com.booking.platform.repository;

import com.booking.platform.entity.Trip;
import com.booking.platform.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository {
    
    @Query("SELECT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH t.bus b " +
           "JOIN FETCH t.operator o " +
           "WHERE r.origin = :origin " +
           "AND r.destination = :destination " +
           "AND DATE(t.departureTime) = DATE(:date) " +
           "AND t.status = 'SCHEDULED' " +
           "AND t.availableSeats > 0")
    Page findAvailableTrips(
        @Param("origin") String origin,
        @Param("destination") String destination,
        @Param("date") LocalDateTime date,
        Pageable pageable
    );
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trip t WHERE t.id = :tripId")
    Optional findByIdWithLock(@Param("tripId") Long tripId);
    
    List findByRouteIdAndDepartureTimeBetween(
        Long routeId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    List findByOperatorIdAndStatus(Long operatorId, TripStatus status);
    
    @Query("SELECT t FROM Trip t WHERE t.departureTime < :now AND t.status = :status")
    List findExpiredTrips(@Param("now") LocalDateTime now, @Param("status") TripStatus status);
}