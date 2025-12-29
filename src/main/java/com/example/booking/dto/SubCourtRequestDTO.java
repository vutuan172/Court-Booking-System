package com.example.booking.dto;

public class SubCourtRequestDTO {
    private String name;
    private Long courtId;
    private Boolean isAvailable;
    private String description;

    public SubCourtRequestDTO() {
    }

    public SubCourtRequestDTO(String name, Long courtId, Boolean isAvailable, String description) {
        this.name = name;
        this.courtId = courtId;
        this.isAvailable = isAvailable;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourtId() {
        return courtId;
    }

    public void setCourtId(Long courtId) {
        this.courtId = courtId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
