package com.hostpitami.service.rates;

import com.hostpitami.api.rates.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RateService {
    List<RatePlanResponse> listPlans(UUID accountId, UUID structureId);
    RatePlanResponse createPlan(UUID accountId, UUID structureId, CreateRatePlanRequest req);
    RatePlanResponse updatePlan(UUID accountId, UUID structureId, UUID planId, UpdateRatePlanRequest req);
    void deletePlan(UUID accountId, UUID structureId, UUID planId);

    RatePlanResponse setPlanRooms(UUID accountId, UUID structureId, UUID planId, SetRatePlanRoomsRequest req);

    List<RateDayResponse> getCalendar(UUID accountId, UUID structureId, UUID planId, UUID roomId, LocalDate from, LocalDate to);
    void setCalendarRange(UUID accountId, UUID structureId, UUID planId, UUID roomId, SetRatesRangeRequest req);
}