package com.hostpitami.api.room.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoomDetailResponse(
        UUID id,
        UUID structureId,
        String name,
        String type,
        String slug,
        int quantity,
        int maxAdults,
        int maxChildren,
        String description,
        String bedInfo,
        Integer sizeMq,
        String amenities,
        boolean archived,
        String coverImageUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}