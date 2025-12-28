package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private Long courtId;
    private String courtName;
    private Long subCourtId;
    private String subCourtName;
    private Long scheduleId;
    private String scheduleDate;
    private String scheduleTime;
    private LocalDateTime bookingTime;
    private String status;
    private BigDecimal totalPrice;
    private String note;
}
