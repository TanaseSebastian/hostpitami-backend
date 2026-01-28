package com.hostpitami.domain.repository;

import com.hostpitami.domain.entity.auth.Role;
import com.hostpitami.domain.entity.auth.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
}