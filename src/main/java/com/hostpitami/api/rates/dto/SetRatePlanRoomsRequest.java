package com.hostpitami.api.rates.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record SetRatePlanRoomsRequest(
        @NotEmpty List<UUID> roomIds
) {}