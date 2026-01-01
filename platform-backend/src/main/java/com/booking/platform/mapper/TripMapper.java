package com.booking.platform.mapper;

import com.booking.platform.entity.Trip;
import com.booking.platform.dto.response.TripResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {
    
    @Mapping(source = "route.origin", target = "origin")
    @Mapping(source = "route.destination", target = "destination")
    @Mapping(source = "operator.name", target = "operatorName")
    @Mapping(source = "bus.busNumber", target = "busNumber")
    @Mapping(source = "bus.busType", target = "busType")
    @Mapping(source = "bus.hasWifi", target = "hasWifi")
    @Mapping(source = "bus.hasAc", target = "hasAc")
    @Mapping(source = "bus.hasRestroom", target = "hasRestroom")
    @Mapping(source = "bus.totalSeats", target = "totalSeats")
    TripResponse toResponse(Trip trip);
}