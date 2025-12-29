package com.example.booking.entity;

import com.example.booking.entity.enums.ScheduleStatus;
import jakarta.persistence.*;

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
    private ScheduleStatus status = ScheduleStatus.AVAILABLE;

    public Schedule() {
    }

    public Schedule(Court court, LocalDate date, LocalTime startTime, LocalTime endTime, BigDecimal price, ScheduleStatus status) {
        this.court = court;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
    }
    
    @PrePersist
    @PreUpdate
    public void setDefaultPrice() {
        if (price == null && court != null) {
            price = court.getBasePricePerHour();
        }
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }
}
