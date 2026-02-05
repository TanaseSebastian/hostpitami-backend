package com.hostpitami.api.room.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoomListItemResponse(
        UUID id,
        UUID structureId,
        String name,
        String type,
        String slug,
        int quantity,
        int maxAdults,
        int maxChildren,
        boolean archived,
        String coverImageUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}