package com.booking.platform.controller;

import com.booking.platform.dto.request.BookingRequest;
import com.booking.platform.dto.response.ApiResponse;
import com.booking.platform.dto.response.BookingResponse;
import com.booking.platform.security.SecurityUtils;
import com.booking.platform.service.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bookings", description = "Booking management endpoints")
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        log.info("Creating booking for user: {}", userEmail);
        
        BookingResponse booking = bookingService.createBooking(request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }
    
    @GetMapping
    @Operation(summary = "Get all bookings for current user")
    public ResponseEntity<ApiResponse<Page>> getUserBookings(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        log.info("Fetching bookings for user: {}", userEmail);
        
        Page bookings = bookingService.getUserBookings(userEmail, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<ApiResponse> getBookingById(@PathVariable Long id) {
        log.info("Fetching booking with id: {}", id);
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/code/{bookingCode}")
    @Operation(summary = "Get booking by booking code")
    public ResponseEntity<ApiResponse> getBookingByCode(@PathVariable String bookingCode) {
        log.info("Fetching booking with code: {}", bookingCode);
        BookingResponse booking = bookingService.getBookingByCode(bookingCode);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm a pending booking")
    public ResponseEntity<ApiResponse> confirmBooking(@PathVariable Long id) {
        log.info("Confirming booking: {}", id);
        BookingResponse booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully", booking));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long id) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        log.info("Cancelling booking {} for user: {}", id, userEmail);
        
        bookingService.cancelBooking(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", null));
    }
}