package com.booking.platform.mapper;

import com.booking.platform.entity.Seat;
import com.booking.platform.dto.response.SeatResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    SeatResponse toResponse(Seat seat);
}