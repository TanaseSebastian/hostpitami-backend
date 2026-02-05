package com.hostpitami.api.structure.dto;

import jakarta.validation.constraints.Size;

public record DuplicateStructureRequest(
        @Size(max = 160) String newName,
        boolean copyRooms,
        boolean copyRates,
        boolean copyPolicies
) {}