package com.booking.platform.repository;

import com.booking.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository {
    
    Optional findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    Optional findByPhone(String phone);
}