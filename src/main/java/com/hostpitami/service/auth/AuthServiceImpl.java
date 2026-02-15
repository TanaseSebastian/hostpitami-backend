package com.hostpitami.service.auth;

import com.hostpitami.api.auth.dto.*;
import com.hostpitami.domain.entity.account.PlanStatus;
import com.hostpitami.domain.entity.auth.PasswordResetToken;
import com.hostpitami.domain.entity.auth.User;
import com.hostpitami.domain.repository.UserRepository;
import com.hostpitami.domain.repository.account.AccountMemberRepository;
import com.hostpitami.domain.repository.account.AccountRepository;
import com.hostpitami.domain.repository.auth.PasswordResetTokenRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import com.hostpitami.security.jwt.JwtService;
import com.hostpitami.service.account.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hostpitami.service.email.EmailService;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * AuthService gestisce tutta la logica di autenticazione e onboarding iniziale.
 *
 * Responsabilità principali:
 * 1) Registrazione: crea User + Account (owner) e genera JWT.
 *    - In register NON crea Structure (scelta prodotto): verrà creata in dashboard.
 *    - AccountService crea anche la membership OWNER (AccountMember).
 *
 * 2) Login: verifica credenziali e genera JWT con claim:
 *    - uid = userId
 *    - aid = activeAccountId (supporto multi-account)
 *
 * 3) Onboarding flags: dopo register/login calcola lo stato di setup dell’account:
 *    - planSelected: true se l’account ha scelto un piano (planStatus != NONE)
 *    - hasAtLeastOneStructure: true se esiste almeno una structure per quell’account
 *    - needsOnboarding: true se manca piano o structure
 *   Questi flag servono al frontend per avviare automaticamente il wizard di setup.
 *
 * 4) Password reset:
 *    - forgotPassword: genera token e lo salva a DB con scadenza (non rivela se l’email esiste)
 *    - resetPassword: valida token (non usato, non scaduto) e aggiorna la password cifrata.
 */


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    private final AccountRepository accounts;
    private final AccountMemberRepository members;
    private final StructureRepository structures;

    private final PasswordResetTokenRepository resetTokens;

    private final AccountService accountService;
    private final EmailService emailService;
    private final String frontendBaseUrl = "http://localhost:5173"; // dev fe react che sto sviluppando insieme

    public AuthServiceImpl(
            UserRepository users,
            PasswordEncoder encoder,
            JwtService jwt,
            AccountRepository accounts,
            AccountMemberRepository members,
            StructureRepository structures,
            PasswordResetTokenRepository resetTokens,
            AccountService accountService,
            EmailService emailService
    ) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.accounts = accounts;
        this.members = members;
        this.structures = structures;
        this.resetTokens = resetTokens;
        this.accountService = accountService;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already used");
        }

        User u = new User();
        u.setFullName(req.fullName().trim());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setActive(true);
        u.setEnabled(true);
        users.save(u);

        // 1) crea account + membership OWNER (lo fa AccountService)
        var account = accountService.createNewAccountForOwner(u.getId());
        UUID accountId = account.getId();

        // 2) JWT con aid
        String token = jwt.generateToken(u.getEmail(), u.getId(), accountId);

        boolean planSelected = accounts.isPlanSelected(accountId, PlanStatus.NONE);
        boolean hasStructure = structures.existsByAccountId(accountId);
        boolean needsOnboarding = !planSelected || !hasStructure;

        return new AuthResponse(
                token, "Bearer",
                u.getId().toString(),
                accountId.toString(),
                planSelected,
                hasStructure,
                needsOnboarding
        );
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        var user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isEnabled() || !user.isActive()) {
            throw new IllegalArgumentException("User disabled");
        }

        if (user.getPasswordHash() == null || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // scegli account default: primo account dove è member (enabled)
        UUID accountId = members.findAccountIdsForUser(user.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No account membership"));

        String token = jwt.generateToken(user.getEmail(), user.getId(), accountId);

        boolean planSelected = accounts.isPlanSelected(accountId, PlanStatus.NONE);
        boolean hasStructure = structures.existsByAccountId(accountId);
        boolean needsOnboarding = !planSelected || !hasStructure;

        return new AuthResponse(
                token, "Bearer",
                user.getId().toString(),
                accountId.toString(),
                planSelected,
                hasStructure,
                needsOnboarding
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest req) {
        String email = req.email().trim().toLowerCase();
        var userOpt = users.findByEmail(email);

        // SECURITY: non rivelare se l’email esiste o no
        if (userOpt.isEmpty()) return;

        var user = userOpt.get();

        String token = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        Instant expires = Instant.now().plusSeconds(60 * 30); // 30 min

        var prt = new PasswordResetToken(
                user.getId(),
                token,
                expires
        );
        resetTokens.save(prt);

        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token + "&email=" + email;
        emailService.sendPasswordResetEmail(email, resetUrl);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        var prt = resetTokens.findByToken(req.token())
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (prt.getUsedAt() != null) throw new IllegalArgumentException("Token already used");
        if (Instant.now().isAfter(prt.getExpiresAt())) throw new IllegalArgumentException("Token expired");

        var user = users.findById(prt.getUserId()).orElseThrow();

        user.setPasswordHash(encoder.encode(req.newPassword()));
        users.save(user);

        prt.setUsedAt(Instant.now());
        resetTokens.save(prt);
    }
}
