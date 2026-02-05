package com.hostpitami.service.booking;

import com.hostpitami.api.booking.dto.CancellationPolicyResponse;
import com.hostpitami.api.booking.dto.UpdateCancellationPolicyRequest;

import java.util.UUID;

public interface CancellationPolicyService {

    CancellationPolicyResponse get(UUID structureId, UUID accountId);

    CancellationPolicyResponse update(
            UUID structureId,
            UUID accountId,
            UpdateCancellationPolicyRequest request
    );
}