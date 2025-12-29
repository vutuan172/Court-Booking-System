package com.example.booking.repository;

import com.example.booking.entity.CourtOwner;
import com.example.booking.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourtOwnerRepository extends JpaRepository<CourtOwner, Long> {
    
    Optional<CourtOwner> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<CourtOwner> findByEmailAndStatus(String email, UserStatus status);
}
