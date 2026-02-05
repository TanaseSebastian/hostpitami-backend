package com.hostpitami.api.rates.dto;

import jakarta.validation.constraints.*;

public record UpdateRatePlanRequest(
        @Size(max=120) String name,
        @Size(max=40) String code,
        Boolean active,
        Boolean appliesToAllRooms,
        Integer basePriceCents,
        Integer defaultMinStay,
        Integer discountPercent
) {}