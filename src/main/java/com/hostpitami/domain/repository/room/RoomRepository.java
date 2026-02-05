package com.hostpitami.domain.repository.room;

import com.hostpitami.domain.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room> findByStructureIdAndStructureAccountId(UUID structureId, UUID accountId);

    Optional<Room> findByIdAndStructureIdAndStructureAccountId(UUID roomId, UUID structureId, UUID accountId);

    boolean existsByStructureIdAndStructureAccountIdAndSlug(UUID structureId, UUID accountId, String slug);

    boolean existsByStructureIdAndStructureAccountIdAndSlugAndIdNot(UUID structureId, UUID accountId, String slug, UUID id);

    boolean existsByIdAndStructureId(UUID roomId, UUID structureId);

    List<Room> findByStructureIdAndArchivedFalse(UUID structureId);
}