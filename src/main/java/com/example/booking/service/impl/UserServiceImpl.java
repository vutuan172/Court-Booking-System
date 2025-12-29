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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final CourtOwnerRepository courtOwnerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, CourtOwnerRepository courtOwnerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courtOwnerRepository = courtOwnerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
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
            CourtOwner owner = new CourtOwner();
            owner.setFullName(request.getFullName());
            owner.setEmail(request.getEmail());
            owner.setPassword(passwordEncoder.encode(request.getPassword()));
            owner.setPhoneNumber(request.getPhoneNumber());
            owner.setBusinessName(request.getBusinessName());
            owner.setBusinessAddress(request.getBusinessAddress());
            owner.setTaxCode(request.getTaxCode());
            owner.setStatus(UserStatus.ACTIVE);
            
            owner = courtOwnerRepository.save(owner);
            
            UserResponseDTO response = new UserResponseDTO();
            response.setId(owner.getId());
            response.setFullName(owner.getFullName());
            response.setEmail(owner.getEmail());
            response.setPhoneNumber(owner.getPhoneNumber());
            response.setStatus(owner.getStatus().name());
            response.setRoles(java.util.Set.of("ROLE_COURT_OWNER"));
            return response;
        }
        
        // Otherwise save to users table
        Role role = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new BookingException("Role ROLE_USER not found"));
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        
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
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setStatus(user.getStatus().name());
        response.setRoles(user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet()));
        return response;
    }
}
