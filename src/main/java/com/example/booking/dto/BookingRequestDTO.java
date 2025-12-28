package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    
    @NotNull(message = "Court ID is required")
    private Long courtId;
    
    private Long subCourtId;
    
    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;
    
    private String note;
}

