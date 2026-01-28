package com.hostpitami.api.auth.controller;

import com.hostpitami.api.auth.dto.*;
import com.hostpitami.domain.entity.account.PlanStatus;
import com.hostpitami.domain.repository.UserRepository;
import com.hostpitami.domain.repository.account.AccountRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import com.hostpitami.security.jwt.JwtService;
import com.hostpitami.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final StructureRepository structureRepository;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            UserRepository userRepository,
            AccountRepository accountRepository,
            StructureRepository structureRepository
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.structureRepository = structureRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            @AuthenticationPrincipal UserDetails principal,
            HttpServletRequest request
    ) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = auth.substring(7);

        UUID accountId = jwtService.getAccountId(token);

        var user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean planSelected = accountRepository.isPlanSelected(accountId, PlanStatus.NONE);
        boolean hasStructure = structureRepository.existsByAccountId(accountId);
        boolean needsOnboarding = !planSelected || !hasStructure;

        return ResponseEntity.ok(new MeResponse(
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                accountId.toString(),
                planSelected,
                hasStructure,
                needsOnboarding
        ));
    }
}
