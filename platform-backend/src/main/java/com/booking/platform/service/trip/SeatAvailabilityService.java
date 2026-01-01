package com.booking.platform.service.trip;

import com.booking.platform.entity.Bus;
import com.booking.platform.entity.Seat;
import com.booking.platform.entity.Trip;
import com.booking.platform.enums.SeatType;
import com.booking.platform.dto.response.SeatResponse;
import com.booking.platform.exception.BookingException;
import com.booking.platform.mapper.SeatMapper;
import com.booking.platform.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatAvailabilityService {
    
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    
    @Transactional
    public void initializeSeats(Trip trip, Bus bus) {
        log.info("Initializing {} seats for trip {}", bus.getTotalSeats(), trip.getId());
        
        List seats = new ArrayList<>();
        
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            Seat seat = Seat.builder()
                .trip(trip)
                .seatNumber(generateSeatNumber(i))
                .seatType(determineSeatType(i, bus.getTotalSeats()))
                .isAvailable(true)
                .isWindow(isWindowSeat(i))
                .build();
            
            seats.add(seat);
        }
        
        seatRepository.saveAll(seats);
        log.info("Seats initialized successfully");
    }
    
    @Transactional(readOnly = true)
    public List getAvailableSeats(Long tripId) {
        List seats = seatRepository.findByTripIdAndIsAvailableTrue(tripId);
        return seats.stream()
            .map(seatMapper::toResponse)
            .toList();
    }
    
    @Transactional
    public void validateAndLockSeats(Long tripId, List seatIds) {
        List seats = seatRepository.findAndLockSeats(tripId, seatIds);
        
        if (seats.size() != seatIds.size()) {
            throw new BookingException("One or more seats are no longer available");
        }
        
        // Lock the seats temporarily (will be finalized when booking is confirmed)
        seats.forEach(seat -> seat.setIsAvailable(false));
        seatRepository.saveAll(seats);
        
        log.info("Locked {} seats for trip {}", seatIds.size(), tripId);
    }
    
    @Transactional
    public void releaseSeats(List seatIds) {
        seatRepository.updateSeatAvailability(seatIds, true);
        log.info("Released {} seats", seatIds.size());
    }
    
    private String generateSeatNumber(int position) {
        int row = (position - 1) / 4 + 1;
        char column = (char) ('A' + (position - 1) % 4);
        return String.format("%d%c", row, column);
    }
    
    private SeatType determineSeatType(int position, int totalSeats) {
        if (position <= 4) {
            return SeatType.PREMIUM; // First row is premium
        } else if (position > totalSeats - 4) {
            return SeatType.VIP; // Last row is VIP
        }
        return SeatType.STANDARD;
    }
    
    private boolean isWindowSeat(int position) {
        int column = (position - 1) % 4;
        return column == 0 || column == 3; // Columns A and D are window seats
    }
}