package com.hostpitami.api.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCancellationPolicyRequest(

        // STANDARD | NON_REFUNDABLE | CUSTOM
        String type,

        @Min(0)
        Integer freeCancellationDays,

        @Min(0)
        @Max(100)
        Integer penaltyPercentage,

        @Size(max = 500)
        String description
) {}