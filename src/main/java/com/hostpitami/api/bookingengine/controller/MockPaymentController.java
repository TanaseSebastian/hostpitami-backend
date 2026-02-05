package com.hostpitami.api.bookingengine.controller;

import com.hostpitami.domain.entity.booking.Booking;
import com.hostpitami.domain.entity.booking.BookingStatus;
import com.hostpitami.domain.repository.booking.BookingRepository;
import com.hostpitami.service.bookingengine.BookingEngineService;
import com.hostpitami.service.mail.MailService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/booking-engine/payment")
public class MockPaymentController {

    private final BookingRepository bookings;
    private final MailService mailService;
    private final BookingEngineService bookingEngineService;

    public MockPaymentController(
            BookingRepository bookings,
            MailService mailService,
            BookingEngineService bookingEngineService
    ) {
        this.bookings = bookings;
        this.mailService = mailService;
        this.bookingEngineService = bookingEngineService;
    }

    @PostMapping("/mock-confirm/{bookingId}")
    public void mockConfirm(@PathVariable UUID bookingId) {

        Booking b = bookings.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (b.getStatus() != BookingStatus.HOLD) {
            throw new IllegalStateException("Booking not in HOLD");
        }

        if (b.getHoldExpiresAt() != null && b.getHoldExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Hold expired");
        }

        //conferma
        b.setStatus(BookingStatus.CONFIRMED);
        b.setPaymentStatus("PAID");
        b.setHoldExpiresAt(null);

        bookings.save(b);

        //side-effect: email + voucher + ics
        String voucherHtml = bookingEngineService.buildVoucherHtml(b.getPublicToken());
        String calendarIcs = bookingEngineService.buildGuestCalendarIcs(b.getPublicToken());

        mailService.sendBookingConfirmation(
                b.getGuest().getEmail(),
                "Prenotazione confermata",
                voucherHtml,
                calendarIcs
        );
    }
}