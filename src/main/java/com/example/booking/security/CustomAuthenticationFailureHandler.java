package com.example.booking.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        // Get the login type from request parameter
        String loginType = request.getParameter("loginType");
        
        // Redirect back to the appropriate login page with error message
        if ("owner".equals(loginType)) {
            response.sendRedirect("/login/owner?error=true");
        } else if ("user".equals(loginType)) {
            response.sendRedirect("/login/user?error=true");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
