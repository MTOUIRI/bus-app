package com.booking.platform.repository;

import com.booking.platform.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository {
    
    Optional findByEmail(String email);
    
    List findByActiveTrue();
    
    @Query("SELECT o FROM Operator o WHERE o.rating >= :minRating AND o.active = true")
    List findTopRatedOperators(Double minRating);
}