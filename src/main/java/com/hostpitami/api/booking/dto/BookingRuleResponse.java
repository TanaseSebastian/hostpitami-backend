package com.hostpitami.api.booking.dto;

public record BookingRuleResponse(
        int minNights,
        int maxNights,
        int minAdvanceDays,
        int maxAdvanceDays,

        boolean allowCheckInMonday,
        boolean allowCheckInTuesday,
        boolean allowCheckInWednesday,
        boolean allowCheckInThursday,
        boolean allowCheckInFriday,
        boolean allowCheckInSaturday,
        boolean allowCheckInSunday,

        String checkInFrom,
        String checkInTo,
        String checkOutBy
) {}