package com.booking.platform.service.trip;

import com.booking.platform.entity.Bus;
import com.booking.platform.entity.Route;
import com.booking.platform.entity.Trip;
import com.booking.platform.enums.TripStatus;
import com.booking.platform.dto.request.CreateTripRequest;
import com.booking.platform.dto.response.TripResponse;
import com.booking.platform.exception.ResourceNotFoundException;
import com.booking.platform.mapper.TripMapper;
import com.booking.platform.repository.BusRepository;
import com.booking.platform.repository.RouteRepository;
import com.booking.platform.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {
    
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final TripMapper tripMapper;
    private final SeatAvailabilityService seatService;
    
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        log.info("Creating new trip for route {} with bus {}", request.getRouteId(), request.getBusId());
        
        Route route = routeRepository.findById(request.getRouteId())
            .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        Bus bus = busRepository.findById(request.getBusId())
            .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        
        Trip trip = Trip.builder()
            .route(route)
            .bus(bus)
            .operator(bus.getOperator())
            .departureTime(request.getDepartureTime())
            .arrivalTime(request.getArrivalTime())
            .basePrice(request.getBasePrice())
            .status(TripStatus.SCHEDULED)
            .availableSeats(bus.getTotalSeats())
            .build();
        
        trip = tripRepository.save(trip);
        
        // Initialize seats for this trip
        seatService.initializeSeats(trip, bus);
        
        log.info("Trip created successfully with id: {}", trip.getId());
        
        return tripMapper.toResponse(trip);
    }
    
    @Transactional
    public void cancelTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        trip.setStatus(TripStatus.CANCELLED);
        tripRepository.save(trip);
        
        log.info("Trip {} cancelled", tripId);
    }
}