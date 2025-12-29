package com.example.booking.entity;

import com.example.booking.entity.enums.BookingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_time", columnList = "booking_time")
})
public class Booking extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_court_id")
    private SubCourt subCourt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;
    
    @Column(nullable = false)
    private LocalDateTime bookingTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    /**
     * Calculate total price = schedule price
     */
    public void calculateTotalPrice() {
        BigDecimal schedulePrice = schedule != null && schedule.getPrice() != null 
            ? schedule.getPrice() 
            : BigDecimal.ZERO;
        this.totalPrice = schedulePrice;
    }

    public Booking() {
    }

    public Booking(User user, Court court, SubCourt subCourt, Schedule schedule, LocalDateTime bookingTime, BookingStatus status, BigDecimal totalPrice, String note) {
        this.user = user;
        this.court = court;
        this.subCourt = subCourt;
        this.schedule = schedule;
        this.bookingTime = bookingTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.note = note;
    }

    public Booking(User user, Court court, SubCourt subCourt, Schedule schedule, LocalDateTime bookingTime, BookingStatus status, BigDecimal totalPrice, String note, List<Review> reviews) {
        this.user = user;
        this.court = court;
        this.subCourt = subCourt;
        this.schedule = schedule;
        this.bookingTime = bookingTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.note = note;
        this.reviews = reviews;
    }
    
    @PrePersist
    public void prePersist() {
        if (bookingTime == null) {
            bookingTime = LocalDateTime.now();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public SubCourt getSubCourt() {
        return subCourt;
    }

    public void setSubCourt(SubCourt subCourt) {
        this.subCourt = subCourt;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
