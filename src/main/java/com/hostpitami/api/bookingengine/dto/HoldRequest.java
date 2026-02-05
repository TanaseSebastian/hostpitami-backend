package com.hostpitami.api.bookingengine.dto;

import java.util.List;

public record HoldRequest(
        QuoteRequest quote,
        GuestLite guest
) {}