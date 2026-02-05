package com.hostpitami.service.booking;

import com.hostpitami.api.booking.dto.CancellationPolicyResponse;
import com.hostpitami.api.booking.dto.UpdateCancellationPolicyRequest;
import com.hostpitami.domain.entity.booking.CancellationPolicy;
import com.hostpitami.domain.entity.structure.Structure;
import com.hostpitami.domain.repository.booking.CancellationPolicyRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional
public class CancellationPolicyServiceImpl implements CancellationPolicyService {

    private final CancellationPolicyRepository policies;
    private final StructureRepository structures;

    public CancellationPolicyServiceImpl(
            CancellationPolicyRepository policies,
            StructureRepository structures
    ) {
        this.policies = policies;
        this.structures = structures;
    }

    @Override
    public CancellationPolicyResponse get(UUID structureId, UUID accountId) {
        CancellationPolicy policy = policies.findByStructureId(structureId)
                .orElseGet(() -> createDefault(structureId, accountId));

        return toResponse(policy);
    }

    @Override
    public CancellationPolicyResponse update(
            UUID structureId,
            UUID accountId,
            UpdateCancellationPolicyRequest req
    ) {
        CancellationPolicy policy = policies.findByStructureId(structureId)
                .orElseThrow(() -> new IllegalArgumentException("Cancellation policy not found"));

        if (req.type() != null) policy.setType(req.type());

        if (req.freeCancellationDays() != null)
            policy.setFreeCancellationDays(req.freeCancellationDays());

        if (req.penaltyPercentage() != null)
            policy.setPenaltyPercentage(req.penaltyPercentage());

        if (req.description() != null)
            policy.setDescription(req.description());

        policy.setUpdatedAt(OffsetDateTime.now());

        return toResponse(policy);
    }

    private CancellationPolicy createDefault(UUID structureId, UUID accountId) {
        Structure structure = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        CancellationPolicy policy = new CancellationPolicy();
        policy.setStructure(structure);

        // DEFAULT: standard policy
        policy.setType("STANDARD");
        policy.setFreeCancellationDays(7);
        policy.setPenaltyPercentage(100);
        policy.setDescription(
                "Cancellazione gratuita fino a 7 giorni prima del check-in. " +
                        "Dopo tale termine verrà addebitato l'intero importo."
        );

        return policies.save(policy);
    }

    private CancellationPolicyResponse toResponse(CancellationPolicy p) {
        return new CancellationPolicyResponse(
                p.getType(),
                p.getFreeCancellationDays(),
                p.getPenaltyPercentage(),
                p.getDescription()
        );
    }
}