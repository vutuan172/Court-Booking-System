package com.example.booking.dto;

public class SubCourtResponseDTO {
    private Long id;
    private String name;
    private Long courtId;
    private String courtName;
    private Boolean isAvailable;
    private String description;

    public SubCourtResponseDTO() {
    }

    public SubCourtResponseDTO(Long id, String name, Long courtId, String courtName, Boolean isAvailable, String description) {
        this.id = id;
        this.name = name;
        this.courtId = courtId;
        this.courtName = courtName;
        this.isAvailable = isAvailable;
        this.description = description;
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

    public Long getCourtId() {
        return courtId;
    }

    public void setCourtId(Long courtId) {
        this.courtId = courtId;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
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
