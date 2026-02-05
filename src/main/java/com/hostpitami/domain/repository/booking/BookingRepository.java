package com.hostpitami.domain.repository.booking;
import com.hostpitami.domain.entity.booking.Booking;
import com.hostpitami.domain.entity.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByRoomIdAndStatusIn(
            UUID roomId,
            List<BookingStatus> statuses
    );

    Optional<Booking> findByPublicToken(String publicToken);

    List<Booking> findByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            UUID roomId,
            LocalDate checkOut,
            LocalDate checkIn
    );
}
