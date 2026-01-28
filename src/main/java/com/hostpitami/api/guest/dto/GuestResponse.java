package com.hostpitami.api.guest.dto;

import java.time.Instant;
import java.util.UUID;

public record GuestResponse(
        UUID id,
        UUID tenantId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String countryCode,
        String documentType,
        String documentNumber,
        String notes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
