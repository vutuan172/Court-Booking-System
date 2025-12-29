package com.example.booking.controller;

import com.example.booking.security.CustomUserDetailsService.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    
    @GetMapping("/")
    public String index(Authentication authentication) {
        // If user is logged in, redirect to dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        // Get custom user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // Check user roles
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isCourtOwner = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_COURT_OWNER"));
        
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isCourtOwner", isCourtOwner);
        model.addAttribute("userName", userDetails.getFullName());
        model.addAttribute("userId", userDetails.getUserId());
        
        return "dashboard";
    }
    
    @GetMapping("/booking")
    public String booking(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return "booking";
    }
    
    @GetMapping("/court-reviews")
    public String courtReviews(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return "court-reviews-new";
    }
    
    @GetMapping("/login")
    public String login() {
        return "redirect:/login/user";
    }
    
    @GetMapping("/login/user")
    public String userLogin() {
        return "auth/user-login";
    }
    
    @GetMapping("/login/owner")
    public String ownerLogin() {
        return "auth/owner-login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/register-owner")
    public String registerOwner() {
        return "register-owner";
    }
    
    @GetMapping("/register/user")
    public String userRegister() {
        return "auth/user-register";
    }
    
    @GetMapping("/register/owner")
    public String ownerRegister() {
        return "auth/owner-register";
    }
}
