package com.hostpitami.api.bookingengine.controller;

import com.hostpitami.api.bookingengine.dto.*;
import com.hostpitami.service.bookingengine.BookingEngineService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/booking-engine")
public class BookingEngineController {

    private final BookingEngineService engine;

    public BookingEngineController(BookingEngineService engine) {
        this.engine = engine;
    }

    @PostMapping("/search")
    public AvailabilitySearchResponse search(@RequestBody AvailabilitySearchRequest req) {
        return engine.search(req);
    }

    @PostMapping("/quote")
    public QuoteResponse quote(@RequestBody QuoteRequest req) {
        return engine.quote(req);
    }

    @PostMapping("/hold")
    public HoldResponse hold(@RequestBody HoldRequest req) {
        return engine.hold(req);
    }

    @PostMapping("/checkout/{bookingId}")
    public CheckoutResponse checkout(@PathVariable("bookingId") java.util.UUID bookingId) {
        return engine.createStripeCheckout(bookingId);
    }

    // link pubblico per calendario ospite (ics)
    @GetMapping(value = "/calendar/{publicToken}.ics", produces = "text/calendar")
    public String guestCalendar(@PathVariable String publicToken) {
        return engine.buildGuestCalendarIcs(publicToken);
    }

    // link pubblico per voucher HTML (MVP)
    @GetMapping(value = "/voucher/{publicToken}", produces = MediaType.TEXT_HTML_VALUE)
    public String voucherHtml(@PathVariable String publicToken) {
        return engine.buildVoucherHtml(publicToken);
    }
}