package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.ReviewResponseDTO;
import com.example.booking.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    
    private final ReviewService reviewService;

    public PublicController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviews(
            @RequestParam(required = false) Long courtId) {
        List<ReviewResponseDTO> reviews;
        if (courtId != null) {
            reviews = reviewService.getApprovedReviewsByCourtId(courtId);
        } else {
            reviews = reviewService.getAllReviews();
        }
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}

