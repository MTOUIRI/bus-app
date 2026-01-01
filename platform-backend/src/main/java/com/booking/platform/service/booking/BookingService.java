package com.booking.platform.service.booking;

import com.booking.platform.entity.Booking;
import com.booking.platform.entity.Seat;
import com.booking.platform.entity.Trip;
import com.booking.platform.entity.User;
import com.booking.platform.enums.BookingStatus;
import com.booking.platform.dto.request.BookingRequest;
import com.booking.platform.dto.response.BookingResponse;
import com.booking.platform.exception.BookingException;
import com.booking.platform.exception.ResourceNotFoundException;
import com.booking.platform.mapper.BookingMapper;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.SeatRepository;
import com.booking.platform.repository.TripRepository;
import com.booking.platform.repository.UserRepository;
import com.booking.platform.service.trip.SeatAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final SeatAvailabilityService seatService;
    private final BookingMapper bookingMapper;
    
    private static final int BOOKING_EXPIRATION_MINUTES = 15;
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        log.info("Creating booking for user: {} on trip: {}", userEmail, request.getTripId());
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Trip trip = tripRepository.findByIdWithLock(request.getTripId())
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        // Validate trip is available
        if (trip.getStatus() != TripStatus.SCHEDULED) {
            throw new BookingException("Trip is not available for booking");
        }
        
        if (trip.getAvailableSeats() < request.getSeatIds().size()) {
            throw new BookingException("Not enough seats available");
        }
        
        // Lock the seats
        seatService.validateAndLockSeats(request.getTripId(), request.getSeatIds());
        
        // Calculate total price
        BigDecimal totalPrice = trip.getBasePrice()
            .multiply(BigDecimal.valueOf(request.getSeatIds().size()));
        
        // Create booking
        Booking booking = Booking.builder()
            .user(user)
            .trip(trip)
            .totalPrice(totalPrice)
            .status(BookingStatus.PENDING)
            .passengerName(request.getPassengerName())
            .passengerEmail(request.getPassengerEmail())
            .passengerPhone(request.getPassengerPhone())
            .expiresAt(LocalDateTime.now().plusMinutes(BOOKING_EXPIRATION_MINUTES))
            .build();
        
        booking = bookingRepository.save(booking);
        
        // Associate seats with booking
        List seats = seatRepository.findAllById(request.getSeatIds());
        seats.forEach(seat -> seat.setBooking(booking));
        seatRepository.saveAll(seats);
        
        // Update trip available seats
        trip.setAvailableSeats(trip.getAvailableSeats() - request.getSeatIds().size());
        tripRepository.save(trip);
        
        log.info("Booking created successfully: {}", booking.getBookingCode());
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Only pending bookings can be confirmed");
        }
        
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BookingException("Booking has expired");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        
        log.info("Booking confirmed: {}", booking.getBookingCode());
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new BookingException("You can only cancel your own bookings");
        }
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }
        
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BookingException("Cannot cancel completed booking");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Release seats
        List seatIds = booking.getSeats().stream()
            .map(Seat::getId)
            .toList();
        
        seatService.releaseSeats(seatIds);
        
        // Update trip available seats
        Trip trip = booking.getTrip();
        trip.setAvailableSeats(trip.getAvailableSeats() + seatIds.size());
        tripRepository.save(trip);
        
        log.info("Booking cancelled: {}", booking.getBookingCode());
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with code: " + bookingCode));
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public Page getUserBookings(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page bookings = bookingRepository.findByUserId(user.getId(), pageable);
        
        return bookings.map(bookingMapper::toResponse);
    }
}