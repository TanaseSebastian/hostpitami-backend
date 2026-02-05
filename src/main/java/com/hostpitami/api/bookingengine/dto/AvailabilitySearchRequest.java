package com.hostpitami.api.bookingengine.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AvailabilitySearchRequest(
        UUID structureId,
        LocalDate checkIn,
        LocalDate checkOut,
        int adults,
        int children
) {}