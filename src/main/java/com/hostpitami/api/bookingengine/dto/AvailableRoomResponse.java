package com.hostpitami.api.bookingengine.dto;

import java.util.UUID;

public record AvailableRoomResponse(
        UUID roomId,
        String roomName,
        String roomType,
        int maxAdults,
        int maxChildren,
        int availableUnits
) {}