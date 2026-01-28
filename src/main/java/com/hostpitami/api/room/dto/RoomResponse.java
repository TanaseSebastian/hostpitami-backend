package com.hostpitami.api.room.dto;

import com.hostpitami.domain.entity.room.RoomType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        UUID tenantId,
        String code,
        String name,
        RoomType type,
        int maxGuests,
        BigDecimal basePricePerNight,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
