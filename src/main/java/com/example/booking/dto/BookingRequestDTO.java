package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;

public class BookingRequestDTO {
    
    @NotNull(message = "Court ID is required")
    private Long courtId;
    
    private Long subCourtId;
    
    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;
    
    private String note;

    public BookingRequestDTO() {
    }

    public BookingRequestDTO(Long courtId, Long subCourtId, Long scheduleId, String note) {
        this.courtId = courtId;
        this.subCourtId = subCourtId;
        this.scheduleId = scheduleId;
        this.note = note;
    }

    public Long getCourtId() {
        return courtId;
    }

    public void setCourtId(Long courtId) {
        this.courtId = courtId;
    }

    public Long getSubCourtId() {
        return subCourtId;
    }

    public void setSubCourtId(Long subCourtId) {
        this.subCourtId = subCourtId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

