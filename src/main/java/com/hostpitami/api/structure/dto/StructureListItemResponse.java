package com.hostpitami.api.structure.dto;

import java.util.UUID;

public record StructureListItemResponse(
        UUID id,
        String name,
        String type,
        String slug,
        boolean published,
        boolean archived,

        String city,
        String country,

        // website
        String domain,
        String websiteTemplate,
        String websiteStatus,  // DRAFT / GENERATING / LIVE / ERROR

        int roomsCount,
        int membersCount
) {}