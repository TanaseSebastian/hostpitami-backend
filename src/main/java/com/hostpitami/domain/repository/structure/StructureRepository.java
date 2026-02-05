package com.hostpitami.domain.repository.structure;

import com.hostpitami.domain.entity.structure.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureRepository extends JpaRepository<Structure, UUID> {

    List<Structure> findByAccountId(UUID accountId);

    Optional<Structure> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByAccountId(UUID accountId);

    boolean existsByAccountIdAndSlug(UUID accountId, String slug);

    boolean existsByAccountIdAndSlugAndIdNot(UUID accountId, String slug, UUID id);

    boolean existsByIdAndAccountId(UUID id, UUID accountId);
}