package com.hostpitami.domain.repository.booking;

import com.hostpitami.domain.entity.booking.BookingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookingRuleRepository extends JpaRepository<BookingRule, UUID> {

    Optional<BookingRule> findByStructureId(UUID structureId);

    boolean existsByStructureId(UUID structureId);
}