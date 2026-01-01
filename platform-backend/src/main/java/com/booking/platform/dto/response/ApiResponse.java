package com.booking.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private T data;
    
    public static  ApiResponse success(T data) {
        return ApiResponse.builder()
            .success(true)
            .data(data)
            .build();
    }
    
    public static  ApiResponse success(String message, T data) {
        return ApiResponse.builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }
    
    public static  ApiResponse error(String message) {
        return ApiResponse.builder()
            .success(false)
            .message(message)
            .build();
    }
}