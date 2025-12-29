package com.example.booking.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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

    public CourtRequestDTO() {
    }

    public CourtRequestDTO(String name, String type, String location, String description, BigDecimal basePricePerHour, String imageUrl, String status) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.basePricePerHour = basePricePerHour;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePricePerHour() {
        return basePricePerHour;
    }

    public void setBasePricePerHour(BigDecimal basePricePerHour) {
        this.basePricePerHour = basePricePerHour;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
