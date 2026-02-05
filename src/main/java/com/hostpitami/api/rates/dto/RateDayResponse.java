package com.hostpitami.api.rates.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RateDayResponse(
        UUID roomId,
        UUID ratePlanId,
        LocalDate date,
        int priceCents,
        int minStay,
        boolean closed
) {}