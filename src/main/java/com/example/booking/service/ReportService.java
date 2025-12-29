package com.example.booking.service;

import com.example.booking.dto.RevenueReportDTO;

import java.time.LocalDate;

public interface ReportService {
    
    RevenueReportDTO getRevenueReport(LocalDate from, LocalDate to);
}

