package com.booking.platform.repository;

import com.booking.platform.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository {
    
    List findByOriginAndDestination(String origin, String destination);
    
    List findByActiveTrue();
    
    @Query("SELECT r FROM Route r WHERE " +
           "(LOWER(r.origin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.destination) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "r.active = true")
    List searchRoutes(@Param("search") String search);
    
    Optional findByOriginAndDestinationAndActiveTrue(String origin, String destination);
}