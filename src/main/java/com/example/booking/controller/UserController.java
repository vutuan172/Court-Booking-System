package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.entity.User;
import com.example.booking.service.BookingService;
import com.example.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final BookingService bookingService;
    private final UserService userService;
    
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getUserBookings(
            @PathVariable Long userId) {
        // Verify current user matches userId or is admin
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("You can only view your own bookings"));
            }
        }
        
        List<BookingResponseDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
}
