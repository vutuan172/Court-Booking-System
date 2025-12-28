package com.example.booking.entity;

import com.example.booking.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "court_owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtOwner extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 200)
    private String businessName; // Tên doanh nghiệp
    
    @Column(length = 500)
    private String businessAddress; // Địa chỉ kinh doanh
    
    @Column(length = 50)
    private String taxCode; // Mã số thuế
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
}
