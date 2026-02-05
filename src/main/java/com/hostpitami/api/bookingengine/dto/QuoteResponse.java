package com.hostpitami.api.bookingengine.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID roomId,
        UUID ratePlanId,
        int nights,
        int baseAmountCents,
        int upsellAmountCents,
        int totalAmountCents,
        String currency,
        Instant expiresAt,
        List<NightPriceLine> nightly
) {}