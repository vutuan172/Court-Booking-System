package com.example.booking.repository;

import com.example.booking.entity.Schedule;
import com.example.booking.entity.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByCourtIdAndDateAndStatus(Long courtId, LocalDate date, ScheduleStatus status);
    
    List<Schedule> findByCourtIdAndDate(Long courtId, LocalDate date);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Schedule s WHERE s.id = :id")
    Optional<Schedule> findByIdForUpdate(@Param("id") Long id);
    
    @Query("SELECT s FROM Schedule s WHERE s.court.id = :courtId AND s.date = :date AND " +
           "s.startTime = :startTime AND s.status = :status")
    Optional<Schedule> findByCourtDateTimeAndStatus(
        @Param("courtId") Long courtId,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("status") ScheduleStatus status
    );
    
    @Query("SELECT s FROM Schedule s WHERE s.date BETWEEN :startDate AND :endDate")
    List<Schedule> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
