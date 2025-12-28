package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.ReviewRequestDTO;
import com.example.booking.dto.ReviewResponseDTO;
import com.example.booking.entity.User;
import com.example.booking.service.ReviewService;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    private final UserService userService;
    
    /**
     * Get all approved reviews for a specific court
     */
    @GetMapping("/court/{courtId}")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getCourtReviews(
            @PathVariable Long courtId) {
        List<ReviewResponseDTO> reviews = reviewService.getApprovedReviewsByCourtId(courtId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }
    
    /**
     * Get review statistics for a court
     */
    @GetMapping("/court/{courtId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourtReviewStats(
            @PathVariable Long courtId) {
        List<ReviewResponseDTO> reviews = reviewService.getApprovedReviewsByCourtId(courtId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate average rating
        double avgRating = reviews.stream()
                .mapToInt(ReviewResponseDTO::getRating)
                .average()
                .orElse(0.0);
        
        // Count ratings by star level
        Map<Integer, Long> ratingCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            long count = reviews.stream()
                    .filter(r -> r.getRating() == rating)
                    .count();
            ratingCounts.put(i, count);
        }
        
        stats.put("averageRating", Math.round(avgRating * 10.0) / 10.0);
        stats.put("totalReviews", reviews.size());
        stats.put("ratingCounts", ratingCounts);
        stats.put("reviews", reviews);
        
        return ResponseEntity.ok(ApiResponse.success("Review statistics retrieved successfully", stats));
    }
    
    /**
     * Create a new review for a booking
     */
    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody ReviewRequestDTO request) {
        
        try {
            // Get current user
            User currentUser = userService.getCurrentUser();
            
            ReviewResponseDTO review = reviewService.createReview(bookingId, request, currentUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Review submitted successfully", review));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
