package com.hostpitami.api.auth.dto;

public record MeResponse(
        String userId,
        String fullName,
        String email,
        String accountId,
        boolean planSelected,
        boolean hasAtLeastOneStructure,
        boolean needsOnboarding
) {}
