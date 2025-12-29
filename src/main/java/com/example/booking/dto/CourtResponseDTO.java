package com.example.booking.dto;

import java.math.BigDecimal;

public class CourtResponseDTO {
    
    private Long id;
    private String name;
    private String type;
    private String location;
    private String description;
    private BigDecimal basePricePerHour;
    private String imageUrl;
    private String status;
    private Long ownerId;

    public CourtResponseDTO() {
    }

    public CourtResponseDTO(Long id, String name, String type, String location, String description, BigDecimal basePricePerHour, String imageUrl, String status, Long ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.basePricePerHour = basePricePerHour;
        this.imageUrl = imageUrl;
        this.status = status;
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
