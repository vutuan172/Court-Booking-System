package com.example.booking.controller;

import com.example.booking.dto.ApiResponse;
import com.example.booking.dto.RegisterRequestDTO;
import com.example.booking.dto.UserResponseDTO;
import com.example.booking.security.CustomUserDetailsService.CustomUserDetails;
import com.example.booking.security.JwtUtil;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        UserResponseDTO user = userService.registerUser(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", user));
    }
    
    @PostMapping("/api-login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> apiLogin(
            @Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Get role
            String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                userDetails.getUsername(), 
                userDetails.getUserId(), 
                role
            );
            
            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("userId", userDetails.getUserId());
            responseData.put("username", userDetails.getUsername());
            responseData.put("role", role);
            responseData.put("tokenType", "Bearer");
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", responseData));
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password"));
        }
    }
    
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
