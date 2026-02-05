package com.hostpitami.api.bookingengine.dto;

import java.util.List;

public record AvailabilitySearchResponse(
        List<AvailableRoomResponse> rooms
) {}