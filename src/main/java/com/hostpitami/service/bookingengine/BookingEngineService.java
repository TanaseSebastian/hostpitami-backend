package com.hostpitami.service.bookingengine;

import com.hostpitami.api.bookingengine.dto.*;

import java.util.UUID;

public interface BookingEngineService {
    AvailabilitySearchResponse search(AvailabilitySearchRequest req);
    QuoteResponse quote(QuoteRequest req);
    HoldResponse hold(HoldRequest req);
    CheckoutResponse createStripeCheckout(UUID bookingId); // mock
    void confirmMockPayment(UUID bookingId);
    String buildVoucherHtml(String publicToken);
    String buildGuestCalendarIcs(String publicToken);
}