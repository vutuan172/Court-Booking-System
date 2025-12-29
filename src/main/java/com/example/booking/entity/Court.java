package com.example.booking.entity;

import com.example.booking.entity.enums.CourtStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "courts")
public class Court extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String type; // football, badminton, tennis, etc.
    
    @Column(nullable = false, length = 200)
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePricePerHour;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourtStatus status = CourtStatus.ACTIVE;
    
    // Owner of the court
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private CourtOwner owner;

    public Court() {
    }

    public Court(String name, String type, String location, String description, BigDecimal basePricePerHour, String imageUrl, CourtStatus status, CourtOwner owner) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.basePricePerHour = basePricePerHour;
        this.imageUrl = imageUrl;
        this.status = status;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePricePerHour() {
        return basePricePerHour;
    }

    public void setBasePricePerHour(BigDecimal basePricePerHour) {
        this.basePricePerHour = basePricePerHour;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CourtStatus getStatus() {
        return status;
    }

    public void setStatus(CourtStatus status) {
        this.status = status;
    }

    public CourtOwner getOwner() {
        return owner;
    }

    public void setOwner(CourtOwner owner) {
        this.owner = owner;
    }
}
