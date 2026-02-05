package com.hostpitami.api.bookingengine.dto;

public record UpsellItemRequest(
        String code,
        int qty
) {}