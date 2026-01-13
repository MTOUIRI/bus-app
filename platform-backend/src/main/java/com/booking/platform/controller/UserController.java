package com.booking.platform.controller;

import com.booking.platform.dto.response.ApiResponse;
import com.booking.platform.dto.response.UserResponse;
import com.booking.platform.mapper.UserMapper;
import com.booking.platform.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User profile endpoints")
public class UserController {
    
    private final UserMapper userMapper;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        var user = SecurityUtils.getCurrentUser();
        log.info("Fetching profile for user: {}", user.getEmail());
        
        UserResponse userResponse = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
}