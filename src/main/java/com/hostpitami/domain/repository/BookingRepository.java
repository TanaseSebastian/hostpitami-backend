package com.hostpitami.domain.repository;

import com.hostpitami.domain.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByTenantId(UUID tenantId);

    // utile per vedere prenotazioni in un range (base)
    List<Booking> findByTenantIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            UUID tenantId, LocalDate to, LocalDate from
    );
}