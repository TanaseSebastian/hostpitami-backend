package com.hostpitami.service.booking;

import com.hostpitami.api.booking.dto.BookingRuleResponse;
import com.hostpitami.api.booking.dto.UpdateBookingRuleRequest;

import java.util.UUID;

public interface BookingRuleService {

    BookingRuleResponse get(UUID structureId, UUID accountId);

    BookingRuleResponse update(UUID structureId, UUID accountId, UpdateBookingRuleRequest req);
}