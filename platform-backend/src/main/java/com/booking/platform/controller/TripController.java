package com.booking.platform.controller;

import com.booking.platform.dto.request.CreateTripRequest;
import com.booking.platform.dto.request.TripSearchRequest;
import com.booking.platform.dto.response.ApiResponse;
import com.booking.platform.dto.response.SeatResponse;
import com.booking.platform.dto.response.TripResponse;
import com.booking.platform.service.trip.SeatAvailabilityService;
import com.booking.platform.service.trip.TripSearchService;
import com.booking.platform.service.trip.TripService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trips", description = "Trip search and management endpoints")
public class TripController {
    
    private final TripSearchService tripSearchService;
    private final TripService tripService;
    private final SeatAvailabilityService seatService;
    
    @GetMapping("/search")
    @Operation(summary = "Search available trips")
    public ResponseEntity<ApiResponse<Page>> searchTrips(
        @Valid @ModelAttribute TripSearchRequest request,
        @PageableDefault(size = 10, sort = "departureTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Searching trips from {} to {} on {}", 
            request.getOrigin(), request.getDestination(), request.getDepartureDate());
        
        Page trips = tripSearchService.searchTrips(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get trip by ID")
    public ResponseEntity<ApiResponse> getTripById(@PathVariable Long id) {
        log.info("Fetching trip with id: {}", id);
        TripResponse trip = tripSearchService.getTripById(id);
        return ResponseEntity.ok(ApiResponse.success(trip));
    }
    
    @GetMapping("/{id}/seats")
    @Operation(summary = "Get available seats for a trip")
    public ResponseEntity<ApiResponse<List>> getAvailableSeats(@PathVariable Long id) {
        log.info("Fetching available seats for trip: {}", id);
        List seats = seatService.getAvailableSeats(id);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new trip (Operator/Admin only)")
    public ResponseEntity<ApiResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        log.info("Creating new trip for route: {}", request.getRouteId());
        TripResponse trip = tripService.createTrip(request);
        return ResponseEntity.ok(ApiResponse.success("Trip created successfully", trip));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel a trip (Operator/Admin only)")
    public ResponseEntity<ApiResponse> cancelTrip(@PathVariable Long id) {
        log.info("Cancelling trip: {}", id);
        tripService.cancelTrip(id);
        return ResponseEntity.ok(ApiResponse.success("Trip cancelled successfully", null));
    }
}