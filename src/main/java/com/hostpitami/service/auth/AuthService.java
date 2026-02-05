package com.hostpitami.service.auth;

import com.hostpitami.api.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
    void forgotPassword(ForgotPasswordRequest req);
    void resetPassword(ResetPasswordRequest req);
}