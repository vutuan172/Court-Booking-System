package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.CourtResponseDTO;
import com.example.booking.dto.ScheduleResponseDTO;
import com.example.booking.service.CourtService;
import com.example.booking.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {
    
    private final CourtService courtService;
    private final ScheduleService scheduleService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourtResponseDTO>>> getAllCourts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {
        List<CourtResponseDTO> courts = courtService.getAllCourts(type, location);
        return ResponseEntity.ok(ApiResponse.success(courts));
    }
    
    @GetMapping("/{courtId}")
    public ResponseEntity<ApiResponse<CourtResponseDTO>> getCourtById(
            @PathVariable Long courtId) {
        CourtResponseDTO court = courtService.getCourtById(courtId);
        return ResponseEntity.ok(ApiResponse.success(court));
    }
    
    @GetMapping("/{courtId}/schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getCourtSchedules(
            @PathVariable Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ScheduleResponseDTO> schedules = scheduleService.getAvailableSchedules(courtId, date);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
}
