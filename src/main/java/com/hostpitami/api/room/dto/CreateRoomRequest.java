package com.hostpitami.api.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank @Size(max=160) String name,
        @NotNull String type,                 // enum RoomType (string)
        @Size(max=120) String slug,           // opzionale
        @Min(1) Integer quantity,
        @Min(1) Integer maxAdults,
        @Min(0) Integer maxChildren,
        @Size(max=2000) String description,
        @Size(max=120) String bedInfo,
        Integer sizeMq,
        @Size(max=2000) String amenities,
        @Size(max=300) String coverImageUrl
) {}