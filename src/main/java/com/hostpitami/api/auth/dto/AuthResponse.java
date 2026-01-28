package com.hostpitami.api.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String userId,
        String accountId,
        boolean planSelected,
        boolean hasAtLeastOneStructure,
        boolean needsOnboarding
) {}
