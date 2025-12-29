package com.example.booking.service.impl;

import com.example.booking.dto.ReviewRequestDTO;
import com.example.booking.dto.ReviewResponseDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Review;
import com.example.booking.entity.User;
import com.example.booking.entity.enums.BookingStatus;
import com.example.booking.entity.enums.ReviewStatus;
import com.example.booking.exception.BookingException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.ReviewRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public ReviewResponseDTO createReview(Long bookingId, ReviewRequestDTO request, Long userId) {
        // Fetch booking
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        
        // Fetch user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Verify user owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingException("You can only review your own bookings");
        }
        
        // Accept both CONFIRMED and DONE bookings for reviews (demo purpose)
        if (booking.getStatus() != BookingStatus.DONE && 
            booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("You can only review confirmed or completed bookings");
        }
        
        // Check if user has already reviewed this booking
        if (reviewRepository.existsByBookingIdAndUserId(bookingId, userId)) {
            throw new BookingException("You have already reviewed this booking");
        }
        
        // Create review
        Review review = new Review(
            booking,
            user,
            request.getRating(),
            request.getComment(),
            request.getImageUrl(),
            ReviewStatus.APPROVED // Auto-approve for simplicity
        );
        
        review = reviewRepository.save(review);
        return convertToDTO(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getApprovedReviewsByCourtId(Long courtId) {
        List<Review> reviews = reviewRepository.findByCourtIdAndStatus(courtId, ReviewStatus.APPROVED);
        return reviews.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private ReviewResponseDTO convertToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setImageUrl(review.getImageUrl());
        dto.setStatus(review.getStatus().name());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
