package com.hostpitami.api.bookingengine.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteRequest(
        UUID structureId,
        UUID roomId,
        UUID ratePlanId,      // opzionale: se null scegliamo il default
        LocalDate checkIn,
        LocalDate checkOut,
        int guestsCount,
        List<UpsellItemRequest> upsells
) {}