package com.example.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(Long id, Long userId, String userName, Long courtId, String courtName, Long subCourtId, String subCourtName, Long scheduleId, String scheduleDate, String scheduleTime, LocalDateTime bookingTime, String status, BigDecimal totalPrice, String note) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.courtId = courtId;
        this.courtName = courtName;
        this.subCourtId = subCourtId;
        this.subCourtName = subCourtName;
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.bookingTime = bookingTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Long getSubCourtId() {
        return subCourtId;
    }

    public void setSubCourtId(Long subCourtId) {
        this.subCourtId = subCourtId;
    }

    public String getSubCourtName() {
        return subCourtName;
    }

    public void setSubCourtName(String subCourtName) {
        this.subCourtName = subCourtName;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
