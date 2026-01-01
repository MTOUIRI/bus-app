package com.booking.platform.service.auth;

import com.booking.platform.entity.User;
import com.booking.platform.enums.UserRole;
import com.booking.platform.dto.request.LoginRequest;
import com.booking.platform.dto.request.RegisterRequest;
import com.booking.platform.dto.response.AuthResponse;
import com.booking.platform.exception.AuthenticationException;
import com.booking.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already registered");
        }
        
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .role(UserRole.USER)
            .enabled(true)
            .accountNonLocked(true)
            .build();
        
        userRepository.save(user);
        
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        log.info("User registered successfully: {}", user.getEmail());
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole().name())
            .build();
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthenticationException("User not found"));
        
        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }
        
        if (!user.isAccountNonLocked()) {
            throw new AuthenticationException("Account is locked");
        }
        
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole().name())
            .build();
    }
    
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new AuthenticationException("User not found"));
        
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new AuthenticationException("Invalid refresh token");
        }
        
        String newAccessToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole().name())
            .build();
    }
}