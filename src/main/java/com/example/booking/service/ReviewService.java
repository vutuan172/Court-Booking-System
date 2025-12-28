package com.example.booking.service;

import com.example.booking.dto.ReviewRequestDTO;
import com.example.booking.dto.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    
    ReviewResponseDTO createReview(Long bookingId, ReviewRequestDTO request, Long userId);
    
    List<ReviewResponseDTO> getApprovedReviewsByCourtId(Long courtId);
    
    List<ReviewResponseDTO> getAllReviews();
}
