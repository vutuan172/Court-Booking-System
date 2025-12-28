package com.example.booking.service.impl;

import com.example.booking.dto.RegisterRequestDTO;
import com.example.booking.dto.UserResponseDTO;
import com.example.booking.entity.CourtOwner;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.entity.enums.UserStatus;
import com.example.booking.exception.BookingException;
import com.example.booking.repository.CourtOwnerRepository;
import com.example.booking.repository.RoleRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final CourtOwnerRepository courtOwnerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserResponseDTO registerUser(RegisterRequestDTO request) {
        // Check if email already exists in both tables
        if (userRepository.existsByEmail(request.getEmail()) || 
            courtOwnerRepository.existsByEmail(request.getEmail())) {
            throw new BookingException("Email already exists");
        }
        
        // If COURT_OWNER, save to court_owners table
        if ("COURT_OWNER".equals(request.getRoleType())) {
            CourtOwner owner = CourtOwner.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .businessName(request.getBusinessName())
                .businessAddress(request.getBusinessAddress())
                .taxCode(request.getTaxCode())
                .status(UserStatus.ACTIVE)
                .build();
            
            owner = courtOwnerRepository.save(owner);
            
            return UserResponseDTO.builder()
                .id(owner.getId())
                .fullName(owner.getFullName())
                .email(owner.getEmail())
                .phoneNumber(owner.getPhoneNumber())
                .status(owner.getStatus().name())
                .roles(java.util.Set.of("ROLE_COURT_OWNER"))
                .build();
        }
        
        // Otherwise save to users table
        Role role = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new BookingException("Role ROLE_USER not found"));
        
        User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .status(UserStatus.ACTIVE)
            .build();
        
        user.addRole(role);
        user = userRepository.save(user);
        
        return convertToDTO(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BookingException("Current user not found"));
    }
    
    @Override
    public UserResponseDTO convertToDTO(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .status(user.getStatus().name())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()))
            .build();
    }
}
