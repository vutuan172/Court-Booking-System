package com.example.booking.service.impl;

import com.example.booking.dto.BookingRequestDTO;
import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.entity.*;
import com.example.booking.entity.enums.BookingStatus;
import com.example.booking.entity.enums.ScheduleStatus;
import com.example.booking.exception.BookingException;
import com.example.booking.exception.DuplicateBookingException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.exception.UnauthorizedException;
import com.example.booking.repository.*;
import com.example.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final SubCourtRepository subCourtRepository;
    
    @Value("${booking.cancellation.min-hours-before:2}")
    private int minHoursBeforeCancellation;

    public BookingServiceImpl(BookingRepository bookingRepository, ScheduleRepository scheduleRepository, CourtRepository courtRepository, UserRepository userRepository, SubCourtRepository subCourtRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
        this.subCourtRepository = subCourtRepository;
    }
    
    @Override
    @Transactional
    public BookingResponseDTO placeBooking(BookingRequestDTO request, Long userId) {
        // Fetch user
        User user = userRepository.findById(Objects.requireNonNull(userId, "User ID cannot be null"))
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Fetch court
        Court court = courtRepository.findById(Objects.requireNonNull(request.getCourtId(), "Court ID cannot be null"))
            .orElseThrow(() -> new ResourceNotFoundException("Court", "id", request.getCourtId()));
        
        // Fetch schedule with pessimistic lock to prevent concurrent booking
        Schedule schedule = scheduleRepository.findByIdForUpdate(request.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", request.getScheduleId()));
        
        // Check if schedule is available
        if (schedule.getStatus() != ScheduleStatus.AVAILABLE) {
            throw new DuplicateBookingException("This schedule slot is no longer available");
        }
        
        // Verify schedule belongs to requested court
        if (!schedule.getCourt().getId().equals(request.getCourtId())) {
            throw new BookingException("Schedule does not belong to the specified court");
        }
        
        // Handle sub-court if provided
        SubCourt subCourt = null;
        if (request.getSubCourtId() != null) {
            subCourt = subCourtRepository.findById(Objects.requireNonNull(request.getSubCourtId(), "SubCourt ID cannot be null"))
                .orElseThrow(() -> new ResourceNotFoundException("SubCourt", "id", request.getSubCourtId()));
            
            // Verify sub-court belongs to the court
            if (!subCourt.getCourt().getId().equals(request.getCourtId())) {
                throw new BookingException("Sub-court does not belong to the specified court");
            }
            
            // Check if sub-court is available
            if (!subCourt.getIsAvailable()) {
                throw new BookingException("This sub-court is not available");
            }
            
            // Check for conflicting bookings in this sub-court for the same time slot
            boolean hasConflict = bookingRepository.existsBySubCourtIdAndScheduleId(
                request.getSubCourtId(), request.getScheduleId());
            if (hasConflict) {
                throw new DuplicateBookingException("This sub-court is already booked for this time slot");
            }
        }
        
        final SubCourt finalSubCourt = subCourt;
        
        try {
            // Create booking with PENDING status (owner needs to approve)
            Booking booking = new Booking(
                user,
                court,
                finalSubCourt,
                schedule,
                LocalDateTime.now(),
                BookingStatus.PENDING,
                schedule.getPrice(),
                request.getNote()
            );
            // Calculate total price
            booking.calculateTotalPrice();
            
            // Mark schedule as booked
            schedule.setStatus(ScheduleStatus.BOOKED);
            scheduleRepository.save(schedule);
            
            // Save booking
            booking = bookingRepository.save(booking);
            
            log.info("Booking created successfully: {}", booking.getId());
            return convertToDTO(booking);
            
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate booking attempt detected", e);
            throw new DuplicateBookingException("This schedule slot has already been booked");
        }
    }
    
    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = findEntityById(id);
        return convertToDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getUserBookings(Long userId) {
        log.info("===== Getting bookings for userId: {} =====", userId);
        List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
        log.info("Found {} bookings in database", bookings.size());
        
        if (!bookings.isEmpty()) {
            bookings.forEach(b -> log.info("Booking ID: {}, Status: {}, User: {}, Court: {}", 
                b.getId(), b.getStatus(), b.getUser().getId(), b.getCourt().getName()));
        }
        
        List<BookingResponseDTO> result = bookings.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        log.info("Returning {} booking DTOs", result.size());
        return result;
    }
    
    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long id, Long userId) {
        Booking booking = findEntityById(id);
        
        // Check if user owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only cancel your own bookings");
        }
        
        // Check if booking can be canceled
        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BookingException("Booking is already canceled");
        }
        
        if (booking.getStatus() == BookingStatus.DONE) {
            throw new BookingException("Cannot cancel a completed booking");
        }
        
        // Check cancellation policy (must be X hours before the schedule)
        LocalDateTime scheduleDateTime = LocalDateTime.of(
            booking.getSchedule().getDate(),
            booking.getSchedule().getStartTime()
        );
        
        long hoursUntilSchedule = Duration.between(LocalDateTime.now(), scheduleDateTime).toHours();
        
        if (hoursUntilSchedule < minHoursBeforeCancellation) {
            throw new BookingException(
                String.format("Booking can only be canceled at least %d hours before the scheduled time", 
                    minHoursBeforeCancellation)
            );
        }
        
        // Cancel booking
        booking.setStatus(BookingStatus.CANCELED);
        
        // Free up the schedule
        Schedule schedule = booking.getSchedule();
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(schedule);
        
        booking = bookingRepository.save(booking);
        
        log.info("Booking canceled: {}", id);
        return convertToDTO(booking);
    }
    
    @Override
    public List<BookingResponseDTO> getAllBookings(LocalDate date, BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByFilters(date, status);
        return bookings.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = findEntityById(id);
        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        
        log.info("Booking {} status updated to {}", id, status);
        return convertToDTO(booking);
    }
    
    @Override
    @Transactional
    public BookingResponseDTO approveBooking(Long id, Long ownerId) {
        // Load booking with all relationships including court owner
        Booking booking = bookingRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        // Verify ownership
        if (!booking.getCourt().getOwner().getId().equals(ownerId)) {
            throw new BookingException("You don't have permission to approve this booking");
        }
        
        // Can only approve pending bookings
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Only pending bookings can be approved");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        
        log.info("Booking {} approved by owner {}", id, ownerId);
        return convertToDTO(booking);
    }
    
    @Override
    @Transactional
    public BookingResponseDTO rejectBooking(Long id, Long ownerId) {
        // Load booking with all relationships including court owner
        Booking booking = bookingRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        // Verify ownership
        if (!booking.getCourt().getOwner().getId().equals(ownerId)) {
            throw new BookingException("You don't have permission to reject this booking");
        }
        
        // Can only reject pending bookings
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Only pending bookings can be rejected");
        }
        
        booking.setStatus(BookingStatus.CANCELED);
        
        // Free up the schedule slot
        Schedule schedule = booking.getSchedule();
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(schedule);
        
        booking = bookingRepository.save(booking);
        
        log.info("Booking {} rejected by owner {}", id, ownerId);
        return convertToDTO(booking);
    }
    
    @Override
    public Booking findEntityById(Long id) {
        return bookingRepository.findById(Objects.requireNonNull(id, "Booking ID cannot be null"))
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }
    
    @Override
    public BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setCourtId(booking.getCourt().getId());
        dto.setCourtName(booking.getCourt().getName());
        dto.setSubCourtId(booking.getSubCourt() != null ? booking.getSubCourt().getId() : null);
        dto.setSubCourtName(booking.getSubCourt() != null ? booking.getSubCourt().getName() : null);
        dto.setScheduleId(booking.getSchedule().getId());
        dto.setScheduleDate(booking.getSchedule().getDate().toString());
        dto.setScheduleTime(booking.getSchedule().getStartTime() + " - " + booking.getSchedule().getEndTime());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus().name());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setNote(booking.getNote());
        return dto;
    }
}
