package com.hostpitami.domain.repository.rates;

import com.hostpitami.domain.entity.rates.RateCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateCalendarRepository extends JpaRepository<RateCalendar, UUID> {
    List<RateCalendar> findByRatePlanIdAndRoomIdAndDateBetween(
            UUID ratePlanId, UUID roomId, LocalDate from, LocalDate to
    );

    void deleteByRatePlanIdAndRoomIdAndDateBetween(
            UUID ratePlanId, UUID roomId, LocalDate from, LocalDate to
    );

    Optional<RateCalendar> findFirstByRatePlanIdAndRoomIdAndDate(
            UUID ratePlanId,
            UUID roomId,
            LocalDate date
    );
}