package com.example.booking.config;

import com.example.booking.entity.*;
import com.example.booking.entity.enums.*;
import com.example.booking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CourtOwnerRepository courtOwnerRepository;
    private final RoleRepository roleRepository;
    private final CourtRepository courtRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${booking.seeding.enabled:true}")
    private boolean seedingEnabled;
    
    @Override
    public void run(String... args) throws Exception {
        if (!seedingEnabled) {
            log.info("Data seeding is disabled");
            return;
        }
        
        if (userRepository.count() > 0) {
            log.info("Database already contains data, skipping seeding");
            return;
        }
        
        log.info("Starting data seeding...");
        
        // Create roles
        Role userRole = createRole("ROLE_USER", "Regular user role - for customers");
        Role adminRole = createRole("ROLE_ADMIN", "Administrator role - system admin");
        
        // Create admin user
        createAdminUser(adminRole);
        
        // Create sample user (customer)
        createSampleUser(userRole);
        
        // Create sample court owner in court_owners table
        CourtOwner courtOwner = createCourtOwner();
        
        // Create courts (owned by court owner)
        List<Court> courts = createCourts(courtOwner);
        
        // Create schedules for next 7 days
        createSchedules(courts);
        
        log.info("Data seeding completed successfully");
    }
    
    private Role createRole(String name, String description) {
        // Check if role already exists
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder()
                .name(name)
                .description(description)
                .build();
            role = roleRepository.save(role);
            log.info("Created role: {}", name);
            return role;
        });
    }
    
    private void createAdminUser(Role adminRole) {
        User admin = User.builder()
            .fullName("Administrator")
            .email("admin@example.com")
            .password(passwordEncoder.encode("strongpassword"))
            .phoneNumber("0123456789")
            .status(UserStatus.ACTIVE)
            .build();
        admin.addRole(adminRole);
        userRepository.save(admin);
        log.info("Created admin user: admin@example.com / strongpassword");
    }
    
    private void createSampleUser(Role userRole) {
        User user = User.builder()
            .fullName("John Doe")
            .email("user@example.com")
            .password(passwordEncoder.encode("password123"))
            .phoneNumber("0987654321")
            .status(UserStatus.ACTIVE)
            .build();
        user.addRole(userRole);
        userRepository.save(user);
        log.info("Created sample user: user@example.com / password123");
    }
    
    private CourtOwner createCourtOwner() {
        CourtOwner owner = CourtOwner.builder()
            .fullName("Nguyễn Văn A")
            .email("owner@example.com")
            .password(passwordEncoder.encode("owner123"))
            .phoneNumber("0912345678")
            .businessName("Công ty TNHH Sân Thể Thao A")
            .businessAddress("123 Đường Giải Phóng, Hai Bà Trưng, Hà Nội")
            .taxCode("0123456789")
            .status(UserStatus.ACTIVE)
            .build();
        owner = courtOwnerRepository.save(owner);
        log.info("Created court owner: owner@example.com / owner123");
        return owner;
    }
    
    private List<Court> createCourts(CourtOwner courtOwner) {
        List<Court> courts = new ArrayList<>();
        
        // Court owners will add their own courts via dashboard
        // No sample courts created by default
        
        log.info("No sample courts created - court owners should add their own courts");
        return courts;
    }
    
    private void createSchedules(List<Court> courts) {
        LocalDate today = LocalDate.now();
        int scheduleCount = 0;
        
        for (int day = 0; day < 7; day++) {
            LocalDate date = today.plusDays(day);
            
            for (Court court : courts) {
                // Create hourly slots from 6 AM to 10 PM
                for (int hour = 6; hour < 22; hour++) {
                    LocalTime startTime = LocalTime.of(hour, 0);
                    LocalTime endTime = LocalTime.of(hour + 1, 0);
                    
                    Schedule schedule = Schedule.builder()
                        .court(court)
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .price(court.getBasePricePerHour())
                        .status(ScheduleStatus.AVAILABLE)
                        .build();
                    
                    scheduleRepository.save(schedule);
                    scheduleCount++;
                }
            }
        }
        
        log.info("Created {} schedules for next 7 days", scheduleCount);
    }
}
