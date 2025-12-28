package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCourtResponseDTO {
    private Long id;
    private String name;
    private Long courtId;
    private String courtName;
    private Boolean isAvailable;
    private String description;
}
