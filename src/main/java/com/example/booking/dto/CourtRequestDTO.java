package com.example.booking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtRequestDTO {
    
    @NotBlank(message = "Court name is required")
    @Size(max = 100, message = "Court name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Court type is required")
    @Size(max = 50, message = "Court type must not exceed 50 characters")
    private String type;
    
    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
    
    private String description;
    
    @NotNull(message = "Base price per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePricePerHour;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    private String status;
}
