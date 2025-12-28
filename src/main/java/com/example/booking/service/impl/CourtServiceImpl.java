package com.example.booking.service.impl;

import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.dto.CourtRequestDTO;
import com.example.booking.dto.CourtResponseDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Court;
import com.example.booking.entity.CourtOwner;
import com.example.booking.entity.User;
import com.example.booking.entity.enums.CourtStatus;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.CourtOwnerRepository;
import com.example.booking.repository.CourtRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {
    
    private final CourtRepository courtRepository;
    private final CourtOwnerRepository courtOwnerRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<CourtResponseDTO> getAllCourts(String type, String location) {
        List<Court> courts;
        
        if (type != null || location != null) {
            courts = courtRepository.findByFilters(type, location, CourtStatus.ACTIVE);
        } else {
            courts = courtRepository.findByStatus(CourtStatus.ACTIVE);
        }
        
        return courts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public CourtResponseDTO getCourtById(Long id) {
        Court court = findEntityById(id);
        return convertToDTO(court);
    }
    
    @Override
    @Transactional
    public CourtResponseDTO createCourt(CourtRequestDTO request) {
        Court court = Court.builder()
            .name(request.getName())
            .type(request.getType())
            .location(request.getLocation())
            .description(request.getDescription())
            .basePricePerHour(request.getBasePricePerHour())
            .imageUrl(request.getImageUrl())
            .status(request.getStatus() != null 
                ? CourtStatus.valueOf(request.getStatus()) 
                : CourtStatus.ACTIVE)
            .build();
        
        court = courtRepository.save(court);
        return convertToDTO(court);
    }
    
    @Override
    @Transactional
    public CourtResponseDTO updateCourt(Long id, CourtRequestDTO request) {
        Court court = findEntityById(id);
        
        court.setName(request.getName());
        court.setType(request.getType());
        court.setLocation(request.getLocation());
        court.setDescription(request.getDescription());
        court.setBasePricePerHour(request.getBasePricePerHour());
        court.setImageUrl(request.getImageUrl());
        
        if (request.getStatus() != null) {
            court.setStatus(CourtStatus.valueOf(request.getStatus()));
        }
        
        court = courtRepository.save(court);
        return convertToDTO(court);
    }
    
    @Override
    @Transactional
    public void deleteCourt(Long id) {
        Court court = findEntityById(id);
        court.setStatus(CourtStatus.INACTIVE);
        courtRepository.save(court);
    }
    
    @Override
    public Court findEntityById(Long id) {
        return courtRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Court", "id", id));
    }
    
    @Override
    public CourtResponseDTO convertToDTO(Court court) {
        return CourtResponseDTO.builder()
            .id(court.getId())
            .name(court.getName())
            .type(court.getType())
            .location(court.getLocation())
            .description(court.getDescription())
            .basePricePerHour(court.getBasePricePerHour())
            .imageUrl(court.getImageUrl())
            .status(court.getStatus().name())
            .ownerId(court.getOwner() != null ? court.getOwner().getId() : null)
            .build();
    }
    
    // Court Owner methods implementation
    
    @Override
    public List<CourtResponseDTO> getCourtsByOwnerId(Long ownerId) {
        List<Court> courts = courtRepository.findAll().stream()
            .filter(court -> court.getOwner() != null && court.getOwner().getId().equals(ownerId))
            .collect(Collectors.toList());
        
        return courts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CourtResponseDTO createCourtForOwner(CourtRequestDTO request, Long ownerId) {
        CourtOwner owner = courtOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Court Owner", "id", ownerId));
        
        Court court = Court.builder()
            .name(request.getName())
            .type(request.getType())
            .location(request.getLocation())
            .description(request.getDescription())
            .basePricePerHour(request.getBasePricePerHour())
            .imageUrl(request.getImageUrl())
            .status(request.getStatus() != null 
                ? CourtStatus.valueOf(request.getStatus()) 
                : CourtStatus.ACTIVE)
            .owner(owner)
            .build();
        
        court = courtRepository.save(court);
        return convertToDTO(court);
    }
    
    @Override
    @Transactional
    public CourtResponseDTO toggleCourtStatus(Long courtId) {
        Court court = findEntityById(courtId);
        
        if (court.getStatus() == CourtStatus.ACTIVE) {
            court.setStatus(CourtStatus.INACTIVE);
        } else {
            court.setStatus(CourtStatus.ACTIVE);
        }
        
        court = courtRepository.save(court);
        return convertToDTO(court);
    }
    
    @Override
    public List<BookingResponseDTO> getBookingsByOwnerId(Long ownerId) {
        // Use optimized query to get all bookings for owner's courts
        List<Booking> bookings = bookingRepository.findByCourtOwnerId(ownerId);
        
        return bookings.stream()
            .map(this::convertBookingToDTO)
            .collect(Collectors.toList());
    }
    
    private BookingResponseDTO convertBookingToDTO(Booking booking) {
        return BookingResponseDTO.builder()
            .id(booking.getId())
            .userId(booking.getUser().getId())
            .userName(booking.getUser().getFullName())
            .scheduleId(booking.getSchedule().getId())
            .courtName(booking.getSchedule().getCourt().getName())
            .scheduleDate(booking.getSchedule().getDate().toString())
            .scheduleTime(booking.getSchedule().getStartTime() + " - " + booking.getSchedule().getEndTime())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus().name())
            .bookingTime(booking.getBookingTime())
            .build();
    }
}
