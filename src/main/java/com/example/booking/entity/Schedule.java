package com.example.booking.entity;

import com.example.booking.entity.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
    name = "schedules",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_schedule_court_date_time", 
            columnNames = {"court_id", "date", "start_time"}
        )
    },
    indexes = {
        @Index(name = "idx_schedule_date", columnList = "date"),
        @Index(name = "idx_schedule_court_date", columnList = "court_id, date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;
    
    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.AVAILABLE;
    
    @PrePersist
    @PreUpdate
    public void setDefaultPrice() {
        if (price == null && court != null) {
            price = court.getBasePricePerHour();
        }
    }
}
