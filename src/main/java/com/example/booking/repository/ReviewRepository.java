package com.example.booking.repository;

import com.example.booking.entity.Review;
import com.example.booking.entity.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByBookingId(Long bookingId);
    
    List<Review> findByUserId(Long userId);
    
    List<Review> findByStatus(ReviewStatus status);
    
    @Query("SELECT r FROM Review r WHERE r.booking.court.id = :courtId AND r.status = :status " +
           "ORDER BY r.createdAt DESC")
    List<Review> findByCourtIdAndStatus(
        @Param("courtId") Long courtId,
        @Param("status") ReviewStatus status
    );
    
    boolean existsByBookingIdAndUserId(Long bookingId, Long userId);
}
