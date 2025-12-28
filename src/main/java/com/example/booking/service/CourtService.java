package com.example.booking.service;

import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.dto.CourtRequestDTO;
import com.example.booking.dto.CourtResponseDTO;
import com.example.booking.entity.Court;

import java.util.List;

public interface CourtService {
    
    List<CourtResponseDTO> getAllCourts(String type, String location);
    
    CourtResponseDTO getCourtById(Long id);
    
    CourtResponseDTO createCourt(CourtRequestDTO request);
    
    CourtResponseDTO updateCourt(Long id, CourtRequestDTO request);
    
    void deleteCourt(Long id);
    
    Court findEntityById(Long id);
    
    CourtResponseDTO convertToDTO(Court court);
    
    // Court Owner methods
    List<CourtResponseDTO> getCourtsByOwnerId(Long ownerId);
    
    CourtResponseDTO createCourtForOwner(CourtRequestDTO request, Long ownerId);
    
    CourtResponseDTO toggleCourtStatus(Long courtId);
    
    List<BookingResponseDTO> getBookingsByOwnerId(Long ownerId);
}
