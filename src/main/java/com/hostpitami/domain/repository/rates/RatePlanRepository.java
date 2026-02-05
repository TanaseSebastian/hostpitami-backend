package com.hostpitami.domain.repository.rates;

import com.hostpitami.domain.entity.rates.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatePlanRepository extends JpaRepository<RatePlan, UUID> {
    List<RatePlan> findByStructureIdOrderByCreatedAtAsc(UUID structureId);
    Optional<RatePlan> findByIdAndStructureId(UUID id, UUID structureId);
    List<RatePlan> findByStructureIdAndActiveTrueOrderByCreatedAtAsc(UUID structureId);
}