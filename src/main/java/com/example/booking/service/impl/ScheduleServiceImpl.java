package com.example.booking.service.impl;

import com.example.booking.dto.ScheduleResponseDTO;
import com.example.booking.entity.Court;
import com.example.booking.entity.Schedule;
import com.example.booking.entity.enums.ScheduleStatus;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.CourtRepository;
import com.example.booking.repository.ScheduleRepository;
import com.example.booking.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;import java.util.Objects;import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final CourtRepository courtRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, CourtRepository courtRepository) {
        this.scheduleRepository = scheduleRepository;
        this.courtRepository = courtRepository;
    }
    
    @Override
    @Transactional
    public List<ScheduleResponseDTO> getAvailableSchedules(Long courtId, LocalDate date) {
        // Check if schedules exist for this court and date
        List<Schedule> existingSchedules = scheduleRepository.findByCourtIdAndDate(courtId, date);
        
        if (existingSchedules.isEmpty()) {
            // Auto-generate schedules for this date
            generateSchedulesForDate(courtId, date);
            existingSchedules = scheduleRepository.findByCourtIdAndDate(courtId, date);
        }
        
        return existingSchedules.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private void generateSchedulesForDate(Long courtId, LocalDate date) {
        Court court = courtRepository.findById(courtId)
            .orElseThrow(() -> new ResourceNotFoundException("Court", "id", courtId));
        
        List<Schedule> schedules = new ArrayList<>();
        
        // Generate hourly slots from 9:00 to 21:00 (9 AM to 9 PM)
        for (int hour = 9; hour < 21; hour++) {
            LocalTime startTime = LocalTime.of(hour, 0);
            LocalTime endTime = LocalTime.of(hour + 1, 0);
            
            Schedule schedule = new Schedule(
                court,
                date,
                startTime,
                endTime,
                court.getBasePricePerHour(),
                ScheduleStatus.AVAILABLE
            );
            
            schedules.add(schedule);
        }
        
        scheduleRepository.saveAll(schedules);
    }
    
    @Override
    public Schedule findEntityById(Long id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
    }
    
    @Override
    public ScheduleResponseDTO convertToDTO(Schedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(schedule.getId());
        dto.setCourtId(schedule.getCourt().getId());
        dto.setCourtName(schedule.getCourt().getName());
        dto.setDate(schedule.getDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setPrice(schedule.getPrice());
        dto.setStatus(schedule.getStatus().name());
        return dto;
    }
}
