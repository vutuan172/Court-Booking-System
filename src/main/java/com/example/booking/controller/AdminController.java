package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.enums.BookingStatus;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.CourtRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final CourtService courtService;
    private final BookingService bookingService;
    private final ReportService reportService;
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    // Court Management
    @GetMapping("/courts")
    public ResponseEntity<ApiResponse<List<CourtResponseDTO>>> getAllCourts() {
        List<CourtResponseDTO> courts = courtService.getAllCourts(null, null);
        return ResponseEntity.ok(ApiResponse.success(courts));
    }
    
    @PostMapping("/courts")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> createCourt(
            @Valid @RequestBody CourtRequestDTO request) {
        CourtResponseDTO court = courtService.createCourt(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Court created successfully", court));
    }
    
    @PutMapping("/courts/{id}")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> updateCourt(
            @PathVariable Long id,
            @Valid @RequestBody CourtRequestDTO request) {
        CourtResponseDTO court = courtService.updateCourt(id, request);
        return ResponseEntity.ok(ApiResponse.success("Court updated successfully", court));
    }
    
    @DeleteMapping("/courts/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.ok(ApiResponse.success("Court deleted successfully", null));
    }
    
    // Booking Management
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getAllBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) BookingStatus status) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings(date, status);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        BookingStatus status = BookingStatus.valueOf(request.get("status"));
        BookingResponseDTO booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated", booking));
    }
    
    // Admin Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourts", courtRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalUsers", userRepository.count());
        
        // Calculate total revenue from completed bookings
        BigDecimal totalRevenue = bookingRepository.findAll().stream()
            .filter(b -> b.getStatus() == BookingStatus.DONE)
            .map(b -> b.getTotalPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        
        return ResponseEntity.ok(stats);
    }
    
    // Reports
    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<RevenueReportDTO>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        RevenueReportDTO report = reportService.getRevenueReport(from, to);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
