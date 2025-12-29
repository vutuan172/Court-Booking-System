package com.example.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sub_courts")
public class SubCourt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    public SubCourt() {
    }

    public SubCourt(Long id, String name, Court court, Boolean isAvailable, String description) {
        this.id = id;
        this.name = name;
        this.court = court;
        this.isAvailable = isAvailable;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
