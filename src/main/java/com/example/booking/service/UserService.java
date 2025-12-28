package com.example.booking.service;

import com.example.booking.dto.RegisterRequestDTO;
import com.example.booking.dto.UserResponseDTO;
import com.example.booking.entity.User;

import java.util.Optional;

public interface UserService {
    
    UserResponseDTO registerUser(RegisterRequestDTO request);
    
    Optional<User> findByEmail(String email);
    
    User getCurrentUser();
    
    UserResponseDTO convertToDTO(User user);
}
