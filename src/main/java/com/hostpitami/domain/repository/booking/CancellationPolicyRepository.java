package com.hostpitami.domain.repository.booking;

import com.hostpitami.domain.entity.booking.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, UUID> {

    Optional<CancellationPolicy> findByStructureId(UUID structureId);
}