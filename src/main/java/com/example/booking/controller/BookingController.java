package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.User;
import com.example.booking.entity.enums.BookingStatus;
import com.example.booking.security.CustomUserDetailsService.CustomUserDetails;
import com.example.booking.service.BookingService;
import com.example.booking.service.ReviewService;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {
        User currentUser = userService.getCurrentUser();
        BookingResponseDTO booking = bookingService.placeBooking(request, currentUser.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Booking created successfully", booking));
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings(
            Authentication authentication) {
        log.info("===== GET /api/bookings/my-bookings called =====");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        log.info("User ID from JWT token: {}, Username: {}", userId, userDetails.getUsername());
        
        List<BookingResponseDTO> bookings = bookingService.getUserBookings(userId);
        log.info("Returning {} bookings to frontend", bookings.size());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        BookingResponseDTO booking = bookingService.getBookingById(id);
        
        // Check if user owns this booking or is admin
        if (!booking.getUserId().equals(currentUser.getId())) {
            boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("You can only view your own bookings"));
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        BookingResponseDTO booking = bookingService.cancelBooking(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Booking canceled successfully", booking));
    }
    
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO request) {
        User currentUser = userService.getCurrentUser();
        ReviewResponseDTO review = reviewService.createReview(id, request, currentUser.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Review created successfully", review));
    }
}
