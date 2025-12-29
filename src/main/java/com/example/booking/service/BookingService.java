package com.example.booking.service;

import com.example.booking.dto.BookingRequestDTO;
import com.example.booking.dto.BookingResponseDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    
    BookingResponseDTO placeBooking(BookingRequestDTO request, Long userId);
    
    BookingResponseDTO getBookingById(Long id);
    
    List<BookingResponseDTO> getUserBookings(Long userId);
    
    BookingResponseDTO cancelBooking(Long id, Long userId);
    
    List<BookingResponseDTO> getAllBookings(LocalDate date, BookingStatus status);
    
    BookingResponseDTO updateBookingStatus(Long id, BookingStatus status);
    
    // Court owner actions
    BookingResponseDTO approveBooking(Long id, Long ownerId);
    
    BookingResponseDTO rejectBooking(Long id, Long ownerId);
    
    Booking findEntityById(Long id);
    
    BookingResponseDTO convertToDTO(Booking booking);
}
