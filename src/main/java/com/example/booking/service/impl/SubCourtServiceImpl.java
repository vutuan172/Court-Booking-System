package com.example.booking.service.impl;

import com.example.booking.dto.SubCourtRequestDTO;
import com.example.booking.dto.SubCourtResponseDTO;
import com.example.booking.entity.Court;
import com.example.booking.entity.SubCourt;
import com.example.booking.repository.CourtRepository;
import com.example.booking.repository.SubCourtRepository;
import com.example.booking.service.SubCourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCourtServiceImpl implements SubCourtService {

    private final SubCourtRepository subCourtRepository;
    private final CourtRepository courtRepository;

    @Override
    @Transactional
    public SubCourtResponseDTO createSubCourt(SubCourtRequestDTO requestDTO) {
        Court court = courtRepository.findById(requestDTO.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        SubCourt subCourt = new SubCourt();
        subCourt.setName(requestDTO.getName());
        subCourt.setCourt(court);
        subCourt.setIsAvailable(requestDTO.getIsAvailable() != null ? requestDTO.getIsAvailable() : true);
        subCourt.setDescription(requestDTO.getDescription());

        SubCourt saved = subCourtRepository.save(subCourt);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public SubCourtResponseDTO updateSubCourt(Long id, SubCourtRequestDTO requestDTO) {
        SubCourt subCourt = subCourtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCourt not found"));

        if (requestDTO.getName() != null) {
            subCourt.setName(requestDTO.getName());
        }
        if (requestDTO.getIsAvailable() != null) {
            subCourt.setIsAvailable(requestDTO.getIsAvailable());
        }
        if (requestDTO.getDescription() != null) {
            subCourt.setDescription(requestDTO.getDescription());
        }

        SubCourt updated = subCourtRepository.save(subCourt);
        return toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteSubCourt(Long id) {
        if (!subCourtRepository.existsById(id)) {
            throw new RuntimeException("SubCourt not found");
        }
        subCourtRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCourtResponseDTO getSubCourtById(Long id) {
        SubCourt subCourt = subCourtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCourt not found"));
        return toResponseDTO(subCourt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCourtResponseDTO> getAllSubCourtsByCourtId(Long courtId) {
        return subCourtRepository.findByCourtId(courtId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCourtResponseDTO> getAvailableSubCourtsByCourtId(Long courtId) {
        return subCourtRepository.findByCourtIdAndIsAvailable(courtId, true)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private SubCourtResponseDTO toResponseDTO(SubCourt subCourt) {
        SubCourtResponseDTO dto = new SubCourtResponseDTO();
        dto.setId(subCourt.getId());
        dto.setName(subCourt.getName());
        dto.setCourtId(subCourt.getCourt().getId());
        dto.setCourtName(subCourt.getCourt().getName());
        dto.setIsAvailable(subCourt.getIsAvailable());
        dto.setDescription(subCourt.getDescription());
        return dto;
    }
}
