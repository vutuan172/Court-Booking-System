package com.example.booking.service.impl;

import com.example.booking.dto.RevenueReportDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.enums.BookingStatus;
import com.example.booking.repository.BookingRepository;
import com.example.booking.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    
    private final BookingRepository bookingRepository;

    public ReportServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public RevenueReportDTO getRevenueReport(LocalDate from, LocalDate to) {
        List<Booking> bookings = bookingRepository.findByDateRangeAndStatuses(
            from, to, Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.DONE)
        );
        
        BigDecimal totalRevenue = bookings.stream()
            .map(Booking::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalBookings = bookings.size();
        
        BigDecimal averageBookingValue = totalBookings > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        RevenueReportDTO report = new RevenueReportDTO();
        report.setTotalRevenue(totalRevenue);
        report.setTotalBookings(totalBookings);
        report.setAverageBookingValue(averageBookingValue);
        return report;
    }
}
