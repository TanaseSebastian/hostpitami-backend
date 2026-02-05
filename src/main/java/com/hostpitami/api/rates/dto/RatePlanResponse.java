package com.hostpitami.api.rates.dto;

import java.util.UUID;

public record RatePlanResponse(
        UUID id,
        UUID structureId,
        String name,
        String code,
        boolean active,
        boolean appliesToAllRooms,
        int basePriceCents,
        int defaultMinStay,
        int discountPercent
) {}