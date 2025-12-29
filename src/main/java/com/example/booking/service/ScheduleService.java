package com.example.booking.service;

import com.example.booking.dto.ScheduleResponseDTO;
import com.example.booking.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    
    List<ScheduleResponseDTO> getAvailableSchedules(Long courtId, LocalDate date);
    
    Schedule findEntityById(Long id);
    
    ScheduleResponseDTO convertToDTO(Schedule schedule);
}
