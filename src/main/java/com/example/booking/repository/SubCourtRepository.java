package com.example.booking.repository;

import com.example.booking.entity.SubCourt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCourtRepository extends JpaRepository<SubCourt, Long> {
    List<SubCourt> findByCourtId(Long courtId);
    List<SubCourt> findByCourtIdAndIsAvailable(Long courtId, Boolean isAvailable);
}
