package com.hostpitami.api.rates.controller;

import com.hostpitami.api.rates.dto.*;
import com.hostpitami.security.auth.AppUserDetails;
import com.hostpitami.security.auth.AuthResolver;
import com.hostpitami.service.rates.RateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/structures/{structureId}/rates")
public class RateAdminController {

    private final RateService rates;
    private final AuthResolver auth;

    public RateAdminController(RateService rates, AuthResolver auth) {
        this.rates = rates;
        this.auth = auth;
    }

    // ---- Rate Plans ----

    @GetMapping("/plans")
    public List<RatePlanResponse> listPlans(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId
    ) {
        UUID accountId = auth.accountId(request);
        return rates.listPlans(accountId, structureId);
    }

    @PostMapping("/plans")
    public RatePlanResponse createPlan(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @Valid @RequestBody CreateRatePlanRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return rates.createPlan(accountId, structureId, req);
    }

    @PatchMapping("/plans/{planId}")
    public RatePlanResponse updatePlan(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID planId,
            @Valid @RequestBody UpdateRatePlanRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return rates.updatePlan(accountId, structureId, planId, req);
    }

    @DeleteMapping("/plans/{planId}")
    public void deletePlan(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID planId
    ) {
        UUID accountId = auth.accountId(request);
        rates.deletePlan(accountId, structureId, planId);
    }

    @PutMapping("/plans/{planId}/rooms")
    public RatePlanResponse setPlanRooms(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID planId,
            @Valid @RequestBody SetRatePlanRoomsRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return rates.setPlanRooms(accountId, structureId, planId, req);
    }

    // ---- Calendar ----

    @GetMapping("/plans/{planId}/rooms/{roomId}/calendar")
    public List<RateDayResponse> getCalendar(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID planId,
            @PathVariable UUID roomId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        UUID accountId = auth.accountId(request);
        return rates.getCalendar(accountId, structureId, planId, roomId, from, to);
    }

    @PutMapping("/plans/{planId}/rooms/{roomId}/calendar")
    public void setCalendarRange(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID planId,
            @PathVariable UUID roomId,
            @Valid @RequestBody SetRatesRangeRequest req
    ) {
        UUID accountId = auth.accountId(request);
        rates.setCalendarRange(accountId, structureId, planId, roomId, req);
    }
}