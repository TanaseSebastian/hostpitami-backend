package com.hostpitami.api.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateRoomRequest(
        @Size(max=160) String name,
        String type,
        @Size(max=120) String slug,
        @Min(1) Integer quantity,
        @Min(1) Integer maxAdults,
        @Min(0) Integer maxChildren,
        @Size(max=2000) String description,
        @Size(max=120) String bedInfo,
        Integer sizeMq,
        @Size(max=2000) String amenities,
        @Size(max=300) String coverImageUrl
) {}