package com.example.booking.security;

import com.example.booking.entity.CourtOwner;
import com.example.booking.entity.User;
import com.example.booking.repository.CourtOwnerRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final CourtOwnerRepository courtOwnerRepository;

    public CustomUserDetailsService(UserRepository userRepository, CourtOwnerRepository courtOwnerRepository) {
        this.userRepository = userRepository;
        this.courtOwnerRepository = courtOwnerRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find in users table first
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getId(),
                mapRolesToAuthorities(user)
            );
        }
        
        // Try to find in court_owners table
        Optional<CourtOwner> ownerOpt = courtOwnerRepository.findByEmail(email);
        if (ownerOpt.isPresent()) {
            CourtOwner owner = ownerOpt.get();
            return new CustomUserDetails(
                owner.getEmail(),
                owner.getPassword(),
                owner.getFullName(),
                owner.getId(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_COURT_OWNER"))
            );
        }
        
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
    
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }
    
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final String fullName;
        private final Long userId;
        
        public CustomUserDetails(String email, String password, String fullName, Long userId,
                                Collection<? extends GrantedAuthority> authorities) {
            super(email, password, authorities);
            this.fullName = fullName;
            this.userId = userId;
        }

        public String getFullName() {
            return fullName;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
