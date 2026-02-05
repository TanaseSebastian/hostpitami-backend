package com.hostpitami.api.bookingengine.dto;

import java.time.LocalDate;

public record NightPriceLine(
        LocalDate date,
        int priceCents,
        int minStay,
        boolean closed
) {}