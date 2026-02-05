package com.hostpitami.service.structure;

import com.hostpitami.api.structure.dto.*;

import java.util.List;
import java.util.UUID;

public interface StructureService {
    List<StructureListItemResponse> list(UUID accountId);

    StructureDetailResponse create(UUID accountId, UUID userId, CreateStructureRequest req);

    StructureDetailResponse get(UUID accountId, UUID structureId);

    StructureDetailResponse update(UUID accountId, UUID structureId, UpdateStructureRequest req);

    void delete(UUID accountId, UUID structureId, boolean hard);

    StructureDetailResponse publish(UUID accountId, UUID structureId);
    StructureDetailResponse unpublish(UUID accountId, UUID structureId);

    StructureDetailResponse archive(UUID accountId, UUID structureId);
    StructureDetailResponse restore(UUID accountId, UUID structureId);

    StructureDetailResponse setDomain(UUID accountId, UUID structureId, SetDomainRequest req);
    StructureDetailResponse setBranding(UUID accountId, UUID structureId, SetBrandingRequest req);

    StructureDetailResponse duplicate(UUID accountId, UUID userId, UUID structureId, DuplicateStructureRequest req);

    WebsitePreviewResponse previewWebsite(UUID accountId, UUID structureId);

    WebsiteGenerationResponse regenerateWebsite(UUID accountId, UUID structureId);
}