package com.hostpitami.api.bookingengine.dto;

public record CheckoutResponse(
        String paymentIntentId,
        String clientSecret
) {}