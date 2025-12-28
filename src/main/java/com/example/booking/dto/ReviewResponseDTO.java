package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    
    private Long id;
    private Long bookingId;
    private Long userId;
    private String userName;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;
}
