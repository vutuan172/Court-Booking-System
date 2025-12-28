package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCourtRequestDTO {
    private String name;
    private Long courtId;
    private Boolean isAvailable;
    private String description;
}
