package com.example.booking.dto;

import java.math.BigDecimal;

public class RevenueReportDTO {
    
    private BigDecimal totalRevenue;
    private Long totalBookings;
    private BigDecimal averageBookingValue;

    public RevenueReportDTO() {
    }

    public RevenueReportDTO(BigDecimal totalRevenue, Long totalBookings, BigDecimal averageBookingValue) {
        this.totalRevenue = totalRevenue;
        this.totalBookings = totalBookings;
        this.averageBookingValue = averageBookingValue;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getAverageBookingValue() {
        return averageBookingValue;
    }

    public void setAverageBookingValue(BigDecimal averageBookingValue) {
        this.averageBookingValue = averageBookingValue;
    }
}
