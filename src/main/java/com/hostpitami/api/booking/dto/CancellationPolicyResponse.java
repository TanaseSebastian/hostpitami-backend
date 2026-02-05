package com.hostpitami.api.booking.dto;

public record CancellationPolicyResponse(
        String type,
        Integer freeCancellationDays,
        Integer penaltyPercentage,
        String description
) {}