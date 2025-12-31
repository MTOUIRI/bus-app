package com.booking.platform.repository;

import com.booking.platform.entity.Payment;
import com.booking.platform.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository {
    
    Optional findByTransactionId(String transactionId);
    
    Optional findByBookingId(Long bookingId);
    
    List findByStatus(PaymentStatus status);
    
    List findByBookingUserId(Long userId);
}