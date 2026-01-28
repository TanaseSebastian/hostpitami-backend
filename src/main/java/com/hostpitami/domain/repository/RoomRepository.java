package com.hostpitami.domain.repository;

import com.hostpitami.domain.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByTenantId(UUID tenantId);
    Optional<Room> findByTenantIdAndCode(UUID tenantId, String code);
}