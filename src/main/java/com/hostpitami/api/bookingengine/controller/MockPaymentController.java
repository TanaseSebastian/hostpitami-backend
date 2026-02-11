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
        bookingEngineService.confirmMockPayment(bookingId);
    }
}