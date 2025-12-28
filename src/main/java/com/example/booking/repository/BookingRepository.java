package com.example.booking.repository;

import com.example.booking.entity.Booking;
import com.example.booking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.user " +
           "JOIN FETCH b.court " +
           "JOIN FETCH b.schedule " +
           "WHERE b.user.id = :userId " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findByUserIdOrderByBookingTimeDesc(@Param("userId") Long userId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.schedule.date = :date")
    List<Booking> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "(:date IS NULL OR b.schedule.date = :date) AND " +
           "(:status IS NULL OR b.status = :status) " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findByFilters(
        @Param("date") LocalDate date,
        @Param("status") BookingStatus status
    );
    
    @Query("SELECT b FROM Booking b WHERE b.schedule.date BETWEEN :startDate AND :endDate " +
           "AND b.status IN :statuses")
    List<Booking> findByDateRangeAndStatuses(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("statuses") List<BookingStatus> statuses
    );
    
    // Find all bookings for courts owned by a specific owner
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.user " +
           "JOIN FETCH b.court c " +
           "JOIN FETCH b.schedule " +
           "WHERE c.owner.id = :ownerId " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findByCourtOwnerId(@Param("ownerId") Long ownerId);
    
    // Find booking by ID with all relationships for approve/reject
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.user " +
           "JOIN FETCH b.court c " +
           "JOIN FETCH c.owner " +
           "JOIN FETCH b.schedule " +
           "WHERE b.id = :id")
    java.util.Optional<Booking> findByIdWithRelations(@Param("id") Long id);
    
    boolean existsByScheduleIdAndStatusNot(Long scheduleId, BookingStatus status);
    
    // Check if sub-court is booked for a specific schedule
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
           "WHERE b.subCourt.id = :subCourtId AND b.schedule.id = :scheduleId " +
           "AND b.status NOT IN ('CANCELLED', 'CANCELED')")
    boolean existsBySubCourtIdAndScheduleId(@Param("subCourtId") Long subCourtId, @Param("scheduleId") Long scheduleId);
}
