package com.booking.platform.repository;

import com.booking.platform.entity.Booking;
import com.booking.platform.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository {
    
    Optional findByBookingCode(String bookingCode);
    
    Page findByUserId(Long userId, Pageable pageable);
    
    List findByUserIdAndStatus(Long userId, BookingStatus status);
    
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.trip t " +
           "JOIN FETCH t.route " +
           "WHERE b.id = :bookingId")
    Optional findByIdWithDetails(@Param("bookingId") Long bookingId);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.expiresAt < :now")
    List findExpiredBookings(@Param("status") BookingStatus status, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trip.id = :tripId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByTripId(@Param("tripId") Long tripId);
}