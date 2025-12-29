package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.SubCourtRequestDTO;
import com.example.booking.dto.SubCourtResponseDTO;
import com.example.booking.service.SubCourtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcourts")
public class SubCourtController {

    private final SubCourtService subCourtService;

    public SubCourtController(SubCourtService subCourtService) {
        this.subCourtService = subCourtService;
    }

    @PostMapping
    @PreAuthorize("hasRole('COURT_OWNER')")
    public ResponseEntity<ApiResponse<SubCourtResponseDTO>> createSubCourt(@RequestBody SubCourtRequestDTO requestDTO) {
        SubCourtResponseDTO response = subCourtService.createSubCourt(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "SubCourt created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COURT_OWNER')")
    public ResponseEntity<ApiResponse<SubCourtResponseDTO>> updateSubCourt(
            @PathVariable Long id,
            @RequestBody SubCourtRequestDTO requestDTO) {
        SubCourtResponseDTO response = subCourtService.updateSubCourt(id, requestDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "SubCourt updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COURT_OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteSubCourt(@PathVariable Long id) {
        subCourtService.deleteSubCourt(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "SubCourt deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCourtResponseDTO>> getSubCourtById(@PathVariable Long id) {
        SubCourtResponseDTO response = subCourtService.getSubCourtById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "SubCourt retrieved successfully", response));
    }

    @GetMapping("/court/{courtId}")
    public ResponseEntity<ApiResponse<List<SubCourtResponseDTO>>> getSubCourtsByCourtId(@PathVariable Long courtId) {
        List<SubCourtResponseDTO> response = subCourtService.getAllSubCourtsByCourtId(courtId);
        return ResponseEntity.ok(new ApiResponse<>(true, "SubCourts retrieved successfully", response));
    }

    @GetMapping("/court/{courtId}/available")
    public ResponseEntity<ApiResponse<List<SubCourtResponseDTO>>> getAvailableSubCourtsByCourtId(@PathVariable Long courtId) {
        List<SubCourtResponseDTO> response = subCourtService.getAvailableSubCourtsByCourtId(courtId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available SubCourts retrieved successfully", response));
    }
}
