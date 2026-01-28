package com.hostpitami.domain.repository;

import com.hostpitami.domain.entity.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);
}