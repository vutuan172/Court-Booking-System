package com.example.booking.entity;

import com.example.booking.entity.enums.BillStatus;
import com.example.booking.entity.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bills")
public class Bill extends BaseEntity {
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status = BillStatus.PENDING;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public Bill() {
    }

    public Bill(BigDecimal amount, PaymentMethod method, BillStatus status, Booking booking) {
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.booking = booking;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
