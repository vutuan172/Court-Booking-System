package com.example.booking.service;

import com.example.booking.dto.SubCourtRequestDTO;
import com.example.booking.dto.SubCourtResponseDTO;

import java.util.List;

public interface SubCourtService {
    SubCourtResponseDTO createSubCourt(SubCourtRequestDTO requestDTO);
    SubCourtResponseDTO updateSubCourt(Long id, SubCourtRequestDTO requestDTO);
    void deleteSubCourt(Long id);
    SubCourtResponseDTO getSubCourtById(Long id);
    List<SubCourtResponseDTO> getAllSubCourtsByCourtId(Long courtId);
    List<SubCourtResponseDTO> getAvailableSubCourtsByCourtId(Long courtId);
}
