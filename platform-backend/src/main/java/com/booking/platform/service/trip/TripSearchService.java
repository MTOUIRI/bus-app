package com.booking.platform.service.trip;

import com.booking.platform.entity.Trip;
import com.booking.platform.dto.request.TripSearchRequest;
import com.booking.platform.dto.response.TripResponse;
import com.booking.platform.mapper.TripMapper;
import com.booking.platform.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripSearchService {
    
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "trips", key = "#request.toString() + #pageable.pageNumber")
    public Page searchTrips(TripSearchRequest request, Pageable pageable) {
        log.info("Searching trips: {} to {} on {}", 
            request.getOrigin(), 
            request.getDestination(), 
            request.getDepartureDate());
        
        LocalDateTime departureDateTime = request.getDepartureDate().atStartOfDay();
        
        Page trips = tripRepository.findAvailableTrips(
            request.getOrigin(),
            request.getDestination(),
            departureDateTime,
            pageable
        );
        
        log.info("Found {} trips", trips.getTotalElements());
        
        return trips.map(tripMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));
        
        return tripMapper.toResponse(trip);
    }
}