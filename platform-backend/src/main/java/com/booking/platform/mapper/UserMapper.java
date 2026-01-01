package com.booking.platform.mapper;

import com.booking.platform.domain.entity.User;
import com.booking.platform.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}