package com.hostpitami.security.auth;

import com.hostpitami.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthResolver {

    private final JwtService jwtService;

    public AuthResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public UUID accountId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing Authorization Bearer token");
        }
        String token = auth.substring(7);
        return jwtService.getAccountId(token);
    }

    public UUID userId(AppUserDetails principal) {
        if (principal == null) throw new IllegalArgumentException("Missing principal");
        return principal.getId();
    }
}