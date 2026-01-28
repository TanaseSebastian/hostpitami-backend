package com.hostpitami.domain.repository.structure;

import com.hostpitami.domain.entity.structure.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StructureRepository extends JpaRepository<Structure, UUID> {
    List<Structure> findByAccountId(UUID accountId);
    boolean existsByAccountId(UUID ownerId);
}