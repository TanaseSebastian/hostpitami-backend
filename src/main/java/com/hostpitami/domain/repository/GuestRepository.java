package com.hostpitami.domain.repository;

import com.hostpitami.domain.entity.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {

    List<Guest> findByTenantId(UUID tenantId);

    Optional<Guest> findByTenantIdAndEmailIgnoreCase(UUID tenantId, String email);
}
