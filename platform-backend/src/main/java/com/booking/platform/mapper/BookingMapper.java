package com.booking.platform.mapper;

import com.booking.platform.entity.Booking;
import com.booking.platform.dto.response.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TripMapper.class, SeatMapper.class})
public interface BookingMapper {
    
    @Mapping(source = "trip", target = "trip")
    @Mapping(source = "seats", target = "seats")
    BookingResponse toResponse(Booking booking);
}