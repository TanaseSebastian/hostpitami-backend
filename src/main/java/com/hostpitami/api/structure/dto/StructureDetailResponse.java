package com.hostpitami.api.structure.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StructureDetailResponse(
        UUID id,
        UUID accountId,
        String name,
        String type,
        String slug,

        String phone,
        String email,
        String addressLine,
        String city,
        String postalCode,
        String country,
        String timezone,
        String currency,
        String checkInTime,
        String checkOutTime,

        boolean published,
        boolean archived,

        // website
        String domain,
        String logoUrl,
        String coverImageUrl,
        String websiteTemplate,
        String primaryColor,
        String accentColor,
        String seoTitle,
        String seoDescription,

        String websiteStatus,
        OffsetDateTime lastGeneratedAt,

        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}