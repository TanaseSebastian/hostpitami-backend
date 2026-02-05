package com.hostpitami.api.bookingengine.dto;

import java.time.Instant;
import java.util.UUID;

public record HoldResponse(
        UUID bookingId,
        String publicToken,
        Instant holdExpiresAt,
        int totalAmountCents,
        String currency
) {}