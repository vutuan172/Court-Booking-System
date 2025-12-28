package com.example.booking.repository;

import com.example.booking.entity.Court;
import com.example.booking.entity.enums.CourtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    
    List<Court> findByStatus(CourtStatus status);
    
    List<Court> findByTypeAndStatus(String type, CourtStatus status);
    
    List<Court> findByLocationContainingIgnoreCaseAndStatus(String location, CourtStatus status);
    
    @Query("SELECT c FROM Court c WHERE " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "c.status = :status")
    List<Court> findByFilters(
        @Param("type") String type,
        @Param("location") String location,
        @Param("status") CourtStatus status
    );
}
