package com.hostpitami.api.room.dto;

import com.hostpitami.domain.entity.room.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RoomRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 160) String name,
        @NotNull RoomType type,
        @Min(1) int maxGuests,
        BigDecimal basePricePerNight,
        Boolean active
) {}
