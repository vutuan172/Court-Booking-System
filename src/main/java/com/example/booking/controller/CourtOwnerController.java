package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.Court;
import com.example.booking.entity.User;
import com.example.booking.security.CustomUserDetailsService.CustomUserDetails;
import com.example.booking.service.BookingService;
import com.example.booking.service.CourtService;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/court-owner")
@PreAuthorize("hasRole('COURT_OWNER')")
@RequiredArgsConstructor
@Slf4j
public class CourtOwnerController {
    
    private final CourtService courtService;
    private final BookingService bookingService;
    private final UserService userService;
    
    // Get all courts owned by current court owner
    @GetMapping("/my-courts")
    public ResponseEntity<ApiResponse<List<CourtResponseDTO>>> getMyCourts(
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        List<CourtResponseDTO> courts = courtService.getCourtsByOwnerId(ownerId);
        return ResponseEntity.ok(ApiResponse.success(courts));
    }
    
    // Create new court
    @PostMapping("/courts")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> createCourt(
            @Valid @RequestBody CourtRequestDTO request,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        CourtResponseDTO court = courtService.createCourtForOwner(request, ownerId);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Court created successfully", court));
    }
    
    // Update own court
    @PutMapping("/courts/{courtId}")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> updateCourt(
            @PathVariable Long courtId,
            @Valid @RequestBody CourtRequestDTO request,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        // Verify ownership
        CourtResponseDTO existingCourt = courtService.getCourtById(courtId);
        if (!existingCourt.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You can only update your own courts"));
        }
        
        CourtResponseDTO court = courtService.updateCourt(courtId, request);
        return ResponseEntity.ok(ApiResponse.success("Court updated successfully", court));
    }
    
    // Toggle court status (ACTIVE/INACTIVE)
    @PatchMapping("/courts/{courtId}/status")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> toggleCourtStatus(
            @PathVariable Long courtId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        // Verify ownership
        CourtResponseDTO existingCourt = courtService.getCourtById(courtId);
        if (!existingCourt.getOwnerId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You can only manage your own courts"));
        }
        
        CourtResponseDTO court = courtService.toggleCourtStatus(courtId);
        return ResponseEntity.ok(ApiResponse.success("Court status updated", court));
    }
    
    // Get bookings for owner's courts
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyCourtBookings(
            Authentication authentication) {
        log.info("===== GET /api/court-owner/bookings called =====");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        log.info("Owner ID from JWT token: {}, Username: {}", ownerId, userDetails.getUsername());
        
        List<BookingResponseDTO> bookings = courtService.getBookingsByOwnerId(ownerId);
        log.info("Returning {} bookings for owner's courts", bookings.size());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    // Approve a booking
    @PutMapping("/bookings/{id}/approve")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> approveBooking(
            @PathVariable Long id,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        BookingResponseDTO booking = bookingService.approveBooking(id, ownerId);
        return ResponseEntity.ok(ApiResponse.success("Booking approved successfully", booking));
    }
    
    // Reject a booking
    @PutMapping("/bookings/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> rejectBooking(
            @PathVariable Long id,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getUserId();
        
        BookingResponseDTO booking = bookingService.rejectBooking(id, ownerId);
        return ResponseEntity.ok(ApiResponse.success("Booking rejected successfully", booking));
    }
}
