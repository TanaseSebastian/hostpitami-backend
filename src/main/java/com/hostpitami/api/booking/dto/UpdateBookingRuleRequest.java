package com.hostpitami.api.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateBookingRuleRequest(

        @Min(1) Integer minNights,
        @Min(1) Integer maxNights,

        @Min(0) Integer minAdvanceDays,
        @Min(1) Integer maxAdvanceDays,

        Boolean allowCheckInMonday,
        Boolean allowCheckInTuesday,
        Boolean allowCheckInWednesday,
        Boolean allowCheckInThursday,
        Boolean allowCheckInFriday,
        Boolean allowCheckInSaturday,
        Boolean allowCheckInSunday,

        @Size(max = 5) String checkInFrom,
        @Size(max = 5) String checkInTo,
        @Size(max = 5) String checkOutBy
) {}