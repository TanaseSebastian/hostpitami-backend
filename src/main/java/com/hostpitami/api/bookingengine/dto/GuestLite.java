package com.hostpitami.api.bookingengine.dto;

public record GuestLite(
        String firstName,
        String lastName,
        String email,
        String phone
) {}