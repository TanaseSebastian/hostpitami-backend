package com.hostpitami.api.rates.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SetRatesRangeRequest(
        @NotNull LocalDate dateFrom,
        @NotNull LocalDate dateTo,

        @Min(0) Integer priceCents,
        @Min(1) Integer minStay,
        Boolean closed
) {}