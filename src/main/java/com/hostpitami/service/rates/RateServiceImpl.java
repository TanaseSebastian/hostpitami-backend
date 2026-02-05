package com.hostpitami.service.rates;

import com.hostpitami.api.rates.dto.*;
import com.hostpitami.domain.entity.rates.RateCalendar;
import com.hostpitami.domain.entity.rates.RatePlan;
import com.hostpitami.domain.entity.rates.RatePlanRoom;
import com.hostpitami.domain.repository.rates.RateCalendarRepository;
import com.hostpitami.domain.repository.rates.RatePlanRepository;
import com.hostpitami.domain.repository.rates.RatePlanRoomRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import com.hostpitami.domain.repository.room.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RateServiceImpl implements RateService {

    private final StructureRepository structures;
    private final RoomRepository rooms;
    private final RatePlanRepository plans;
    private final RatePlanRoomRepository planRooms;
    private final RateCalendarRepository calendar;

    public RateServiceImpl(
            StructureRepository structures,
            RoomRepository rooms,
            RatePlanRepository plans,
            RatePlanRoomRepository planRooms,
            RateCalendarRepository calendar
    ) {
        this.structures = structures;
        this.rooms = rooms;
        this.plans = plans;
        this.planRooms = planRooms;
        this.calendar = calendar;
    }

    private void assertStructureOwned(UUID accountId, UUID structureId) {
        if (!structures.existsByIdAndAccountId(structureId, accountId))
            throw new IllegalArgumentException("Structure not found");
    }

    private RatePlan getPlanOrThrow(UUID structureId, UUID planId) {
        return plans.findByIdAndStructureId(planId, structureId)
                .orElseThrow(() -> new IllegalArgumentException("RatePlan not found"));
    }

    @Override
    public List<RatePlanResponse> listPlans(UUID accountId, UUID structureId) {
        assertStructureOwned(accountId, structureId);
        return plans.findByStructureIdOrderByCreatedAtAsc(structureId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RatePlanResponse createPlan(UUID accountId, UUID structureId, CreateRatePlanRequest req) {
        assertStructureOwned(accountId, structureId);

        var structure = structures.findById(structureId).orElseThrow();

        RatePlan p = new RatePlan();
        p.setStructure(structure);
        p.setName(req.name().trim());
        p.setCode(req.code().trim().toUpperCase(Locale.ROOT));
        p.setAppliesToAllRooms(req.appliesToAllRooms());
        p.setBasePriceCents(req.basePriceCents());
        p.setDefaultMinStay(req.defaultMinStay());
        p.setDiscountPercent(req.discountPercent());
        p.setActive(true);

        return toResponse(plans.save(p));
    }

    @Override
    @Transactional
    public RatePlanResponse updatePlan(UUID accountId, UUID structureId, UUID planId, UpdateRatePlanRequest req) {
        assertStructureOwned(accountId, structureId);
        RatePlan p = getPlanOrThrow(structureId, planId);

        if (req.name() != null && !req.name().isBlank()) p.setName(req.name().trim());
        if (req.code() != null && !req.code().isBlank()) p.setCode(req.code().trim().toUpperCase(Locale.ROOT));
        if (req.active() != null) p.setActive(req.active());
        if (req.appliesToAllRooms() != null) p.setAppliesToAllRooms(req.appliesToAllRooms());
        if (req.basePriceCents() != null) p.setBasePriceCents(req.basePriceCents());
        if (req.defaultMinStay() != null) p.setDefaultMinStay(req.defaultMinStay());
        if (req.discountPercent() != null) p.setDiscountPercent(req.discountPercent());

        return toResponse(plans.save(p));
    }

    @Override
    @Transactional
    public void deletePlan(UUID accountId, UUID structureId, UUID planId) {
        assertStructureOwned(accountId, structureId);
        RatePlan p = getPlanOrThrow(structureId, planId);

        // cleanup mapping camere e calendario
        planRooms.deleteByRatePlanId(planId);
        // NB: calendar ha ID proprio -> cancellazione bulk la fai per room/range,
        // per MVP puoi lasciare i record "orfani" e pulire più avanti,
        // ma meglio fare query custom deleteByRatePlanId (se vuoi, te la aggiungo).
        plans.delete(p);
    }

    @Override
    @Transactional
    public RatePlanResponse setPlanRooms(UUID accountId, UUID structureId, UUID planId, SetRatePlanRoomsRequest req) {
        assertStructureOwned(accountId, structureId);
        RatePlan p = getPlanOrThrow(structureId, planId);

        // se plan è "appliesToAllRooms", puoi anche ignorare mapping
        p.setAppliesToAllRooms(false);

        // validate che le room appartengano alla struttura
        for (UUID roomId : req.roomIds()) {
            if (!rooms.existsByIdAndStructureId(roomId, structureId))
                throw new IllegalArgumentException("Room not found in structure: " + roomId);
        }

        planRooms.deleteByRatePlanId(planId);
        for (UUID roomId : req.roomIds()) {
            RatePlanRoom link = new RatePlanRoom();
            link.setRatePlanId(planId);
            link.setRoomId(roomId);
            planRooms.save(link);
        }

        return toResponse(plans.save(p));
    }

    @Override
    public List<RateDayResponse> getCalendar(UUID accountId, UUID structureId, UUID planId, UUID roomId, LocalDate from, LocalDate to) {
        assertStructureOwned(accountId, structureId);
        getPlanOrThrow(structureId, planId);

        if (!rooms.existsByIdAndStructureId(roomId, structureId))
            throw new IllegalArgumentException("Room not found");

        return calendar.findByRatePlanIdAndRoomIdAndDateBetween(planId, roomId, from, to).stream()
                .map(rc -> new RateDayResponse(roomId, planId, rc.getDate(), rc.getPriceCents(), rc.getMinStay(), rc.isClosed()))
                .toList();
    }

    @Override
    @Transactional
    public void setCalendarRange(UUID accountId, UUID structureId, UUID planId, UUID roomId, SetRatesRangeRequest req) {
        assertStructureOwned(accountId, structureId);
        RatePlan plan = getPlanOrThrow(structureId, planId);

        if (!rooms.existsByIdAndStructureId(roomId, structureId))
            throw new IllegalArgumentException("Room not found");

        LocalDate start = req.dateFrom();
        LocalDate end = req.dateTo();
        if (end.isBefore(start)) throw new IllegalArgumentException("dateTo < dateFrom");

        // Strategia MVP: upsert giorno per giorno (range piccoli va benissimo)
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {

            // prendi record esistente per quel giorno oppure crealo
            // (per MVP: query per range + map, qui semplifico con find range e map sarebbe meglio)
            List<RateCalendar> existing = calendar.findByRatePlanIdAndRoomIdAndDateBetween(planId, roomId, d, d);

            RateCalendar rc = existing.isEmpty() ? new RateCalendar() : existing.get(0);

            rc.setRatePlanId(planId);
            rc.setRoomId(roomId);
            rc.setDate(d);

            // Se non impostano price, usa base price del plan (ottimo per "riempimento calendario")
            int price = (req.priceCents() != null) ? req.priceCents() : plan.getBasePriceCents();
            rc.setPriceCents(price);

            if (req.minStay() != null) rc.setMinStay(req.minStay());
            else if (existing.isEmpty()) rc.setMinStay(plan.getDefaultMinStay());

            if (req.closed() != null) rc.setClosed(req.closed());

            calendar.save(rc);
        }
    }

    private RatePlanResponse toResponse(RatePlan p) {
        return new RatePlanResponse(
                p.getId(),
                p.getStructure().getId(),
                p.getName(),
                p.getCode(),
                p.isActive(),
                p.isAppliesToAllRooms(),
                p.getBasePriceCents(),
                p.getDefaultMinStay(),
                p.getDiscountPercent()
        );
    }
}