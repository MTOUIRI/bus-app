package com.booking.platform.service.booking;

import com.booking.platform.entity.Booking;
import com.booking.platform.enums.BookingStatus;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.service.trip.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingExpirationService {
    
    private final BookingRepository bookingRepository;
    private final SeatAvailabilityService seatService;
    
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void expireOldBookings() {
        List expiredBookings = bookingRepository.findExpiredBookings(
            BookingStatus.PENDING,
            LocalDateTime.now()
        );
        
        if (!expiredBookings.isEmpty()) {
            log.info("Found {} expired bookings to process", expiredBookings.size());
            
            expiredBookings.forEach(booking -> {
                booking.setStatus(BookingStatus.EXPIRED);
                
                // Release seats
                List seatIds = booking.getSeats().stream()
                    .map(seat -> seat.getId())
                    .toList();
                
                seatService.releaseSeats(seatIds);
                
                // Update trip available seats
                booking.getTrip().setAvailableSeats(
                    booking.getTrip().getAvailableSeats() + seatIds.size()
                );
                
                log.info("Expired booking: {}", booking.getBookingCode());
            });
            
            bookingRepository.saveAll(expiredBookings);
        }
    }
}