package com.hostpitami.api.rates.dto;

import jakarta.validation.constraints.*;

public record CreateRatePlanRequest(
        @NotBlank @Size(max=120) String name,
        @NotBlank @Size(max=40) String code,
        boolean appliesToAllRooms,
        @Min(0) int basePriceCents,
        @Min(1) int defaultMinStay,
        @Min(0) @Max(100) int discountPercent
) {}