package com.example.booking.service.impl;

import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.dto.CourtRequestDTO;
import com.example.booking.dto.CourtResponseDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Court;
import com.example.booking.entity.CourtOwner;
import com.example.booking.entity.enums.CourtStatus;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.CourtOwnerRepository;
import com.example.booking.repository.CourtRepository;
import com.example.booking.service.CourtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourtServiceImpl implements CourtService {
    
    private final CourtRepository courtRepository;
    private final CourtOwnerRepository courtOwnerRepository;
    private final BookingRepository bookingRepository;

    public CourtServiceImpl(CourtRepository courtRepository, CourtOwnerRepository courtOwnerRepository, BookingRepository bookingRepository) {
        this.courtRepository = courtRepository;
        this.courtOwnerRepository = courtOwnerRepository;
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public List<CourtResponseDTO> getAllCourts(String type, String location) {
        List<Court> courts;
        
        if (type != null || location != null) {
            courts = courtRepository.findByFilters(type, location, CourtStatus.ACTIVE);
        } else {
            courts = courtRepository.findAll();
        }
        
        return courts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourtResponseDTO> findCourts(CourtStatus status, String type) {
        List<Court> courts = courtRepository.findByFilters(status, type);

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
        Court court = new Court(
            request.getName(),
            request.getType(),
            request.getLocation(),
            request.getDescription(),
            request.getBasePricePerHour(),
            request.getImageUrl(),
            request.getStatus() != null 
                ? CourtStatus.valueOf(request.getStatus()) 
                : CourtStatus.ACTIVE,
            null
        );
        
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
        return courtRepository.findById(Objects.requireNonNull(id, "Court ID cannot be null"))
            .orElseThrow(() -> new ResourceNotFoundException("Court", "id", id));
    }
    
    @Override
    public CourtResponseDTO convertToDTO(Court court) {
        CourtResponseDTO dto = new CourtResponseDTO();
        dto.setId(court.getId());
        dto.setName(court.getName());
        dto.setType(court.getType());
        dto.setLocation(court.getLocation());
        dto.setDescription(court.getDescription());
        dto.setBasePricePerHour(court.getBasePricePerHour());
        dto.setImageUrl(court.getImageUrl());
        dto.setStatus(court.getStatus().name());
        dto.setOwnerId(court.getOwner() != null ? court.getOwner().getId() : null);
        return dto;
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
        CourtOwner owner = courtOwnerRepository.findById(Objects.requireNonNull(ownerId))
            .orElseThrow(() -> new ResourceNotFoundException("Court Owner", "id", ownerId));
        
        Court court = new Court(
            request.getName(),
            request.getType(),
            request.getLocation(),
            request.getDescription(),
            request.getBasePricePerHour(),
            request.getImageUrl(),
            request.getStatus() != null 
                ? CourtStatus.valueOf(request.getStatus()) 
                : CourtStatus.ACTIVE,
            owner
        );
        
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
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setScheduleId(booking.getSchedule().getId());
        dto.setCourtName(booking.getSchedule().getCourt().getName());
        dto.setScheduleDate(booking.getSchedule().getDate().toString());
        dto.setScheduleTime(booking.getSchedule().getStartTime() + " - " + booking.getSchedule().getEndTime());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus().name());
        dto.setBookingTime(booking.getBookingTime());
        return dto;
    }
}
