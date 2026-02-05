package com.hostpitami.service.bookingengine;

import com.hostpitami.api.bookingengine.dto.*;
import com.hostpitami.domain.entity.booking.Booking;
import com.hostpitami.domain.entity.booking.BookingStatus;
import com.hostpitami.domain.entity.rates.RateCalendar;
import com.hostpitami.domain.entity.rates.RatePlan;
import com.hostpitami.domain.repository.booking.BookingRepository;
import com.hostpitami.domain.repository.rates.RateCalendarRepository;
import com.hostpitami.domain.repository.rates.RatePlanRepository;
import com.hostpitami.domain.repository.rates.RatePlanRoomRepository;
import com.hostpitami.domain.repository.room.RoomRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import com.hostpitami.service.mail.MailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingEngineServiceImpl implements BookingEngineService {

    private final StructureRepository structures;
    private final RoomRepository rooms;
    private final BookingRepository bookings;
    private final RatePlanRepository ratePlans;
    private final RatePlanRoomRepository planRooms;
    private final RateCalendarRepository calendar;
    private final MailService mail;

    public BookingEngineServiceImpl(
            StructureRepository structures,
            RoomRepository rooms,
            BookingRepository bookings,
            RatePlanRepository ratePlans,
            RatePlanRoomRepository planRooms,
            RateCalendarRepository calendar,
            MailService mail
    ) {
        this.structures = structures;
        this.rooms = rooms;
        this.bookings = bookings;
        this.ratePlans = ratePlans;
        this.planRooms = planRooms;
        this.calendar = calendar;
        this.mail = mail;
    }

    @Override
    public AvailabilitySearchResponse search(AvailabilitySearchRequest req) {
        var structure = structures.findById(req.structureId())
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        LocalDate in = req.checkIn();
        LocalDate out = req.checkOut();
        if (out.isBefore(in) || out.equals(in)) throw new IllegalArgumentException("Invalid dates");

        var roomList = rooms.findByStructureIdAndArchivedFalse(structure.getId());

        List<AvailableRoomResponse> response = new ArrayList<>();

        for (var r : roomList) {
            if (req.adults() > r.getMaxAdults()) continue;
            if (req.children() > r.getMaxChildren()) continue;

            int usedUnits = countUsedUnits(r.getId(), in, out);
            int available = Math.max(0, r.getQuantity() - usedUnits);

            if (available > 0) {
                response.add(new AvailableRoomResponse(
                        r.getId(),
                        r.getName(),
                        r.getType().name(),
                        r.getMaxAdults(),
                        r.getMaxChildren(),
                        available
                ));
            }
        }

        return new AvailabilitySearchResponse(response);
    }

    @Override
    public QuoteResponse quote(QuoteRequest req) {
        var structure = structures.findById(req.structureId())
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        var room = rooms.findById(req.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!room.getStructure().getId().equals(structure.getId()))
            throw new IllegalArgumentException("Room not in structure");

        int nights = (int) ChronoUnit.DAYS.between(req.checkIn(), req.checkOut());
        if (nights <= 0) throw new IllegalArgumentException("Invalid dates");

        var plan = pickPlan(structure.getId(), room.getId(), req.ratePlanId());

        List<NightPriceLine> nightly = new ArrayList<>();
        int base = 0;

        for (LocalDate d = req.checkIn(); d.isBefore(req.checkOut()); d = d.plusDays(1)) {
            RateCalendar rc = calendar.findFirstByRatePlanIdAndRoomIdAndDate(plan.getId(), room.getId(), d)
                    .orElse(null);

            int price = (rc != null) ? rc.getPriceCents() : plan.getBasePriceCents();
            int minStay = (rc != null) ? rc.getMinStay() : plan.getDefaultMinStay();
            boolean closed = (rc != null) && rc.isClosed();

            if (closed) throw new IllegalArgumentException("Room closed on " + d);
            if (nights < minStay) throw new IllegalArgumentException("Min stay is " + minStay);

            nightly.add(new NightPriceLine(d, price, minStay, false));
            base += price;
        }

        int upsell = computeUpsellAmount(req.upsells(), nights);
        int total = base + upsell;

        return new QuoteResponse(
                room.getId(),
                plan.getId(),
                nights,
                base,
                upsell,
                total,
                "eur",
                Instant.now().plus(10, ChronoUnit.MINUTES),
                nightly
        );
    }

    @Override
    @Transactional
    public HoldResponse hold(HoldRequest req) {
        QuoteResponse q = quote(req.quote());

        var room = rooms.findById(req.quote().roomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        int used = countUsedUnits(room.getId(), req.quote().checkIn(), req.quote().checkOut());
        if (used >= room.getQuantity()) throw new IllegalStateException("No availability");

        Booking b = new Booking();
        b.setRoom(room);
        b.setCheckInDate(req.quote().checkIn());
        b.setCheckOutDate(req.quote().checkOut());
        b.setGuestsCount(req.quote().guestsCount());
        b.setStatus(BookingStatus.HOLD);
        b.setPaymentStatus("PENDING");
        b.setTotalPrice(BigDecimal.valueOf(q.totalAmountCents()).movePointLeft(2));
        b.setHoldExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));
        b.setPublicToken(UUID.randomUUID().toString().replace("-", ""));

        // dati ospite “inline”
        b.setGuestFirstName(req.guest().firstName());
        b.setGuestLastName(req.guest().lastName());
        b.setGuestEmail(req.guest().email());
        b.setGuestPhone(req.guest().phone());

        Booking saved = bookings.save(b);

        return new HoldResponse(
                saved.getId(),
                saved.getPublicToken(),
                saved.getHoldExpiresAt(),
                q.totalAmountCents(),
                q.currency()
        );
    }

    @Override
    public CheckoutResponse createStripeCheckout(UUID bookingId) {
        Booking b = bookings.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (b.getStatus() != BookingStatus.HOLD) throw new IllegalArgumentException("Booking not in HOLD");
        if (b.getHoldExpiresAt() != null && b.getHoldExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Hold expired");

        String fakeIntentId = "mock_intent_" + b.getId();
        String fakeClientSecret = "mock_secret_" + b.getPublicToken();
        return new CheckoutResponse(fakeIntentId, fakeClientSecret);
    }

    @Override
    @Transactional
    public void confirmMockPayment(UUID bookingId) {
        Booking b = bookings.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (b.getStatus() != BookingStatus.HOLD) throw new IllegalStateException("Booking not in HOLD");
        if (b.getHoldExpiresAt() != null && b.getHoldExpiresAt().isBefore(Instant.now())) throw new IllegalStateException("Hold expired");

        b.setStatus(BookingStatus.CONFIRMED);
        b.setPaymentStatus("PAID");
        b.setHoldExpiresAt(null);
        bookings.save(b);

        mail.sendBookingConfirmation(
                b.getGuestEmail(),
                "Prenotazione confermata",
                buildVoucherHtml(b.getPublicToken()),
                buildGuestCalendarIcs(b.getPublicToken())
        );
    }

    @Override
    public String buildVoucherHtml(String publicToken) {
        Booking b = bookings.findByPublicToken(publicToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        return """
                <html><body style="font-family:Arial">
                <h2>Prenotazione confermata ✅</h2>
                <p><b>Ospite:</b> %s %s</p>
                <p><b>Check-in:</b> %s<br/><b>Check-out:</b> %s</p>
                <p><b>Codice:</b> %s</p>
                </body></html>
                """.formatted(
                b.getGuestFirstName(),
                b.getGuestLastName(),
                b.getCheckInDate(),
                b.getCheckOutDate(),
                publicToken
        );
    }

    @Override
    public String buildGuestCalendarIcs(String publicToken) {
        Booking b = bookings.findByPublicToken(publicToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        String dtstamp = Instant.now().toString().replace("-", "").replace(":", "").split("\\.")[0] + "Z";
        String dtStart = b.getCheckInDate().toString().replace("-", "");
        String dtEnd = b.getCheckOutDate().toString().replace("-", "");

        return ("""
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Hostpitami//Booking//IT
                BEGIN:VEVENT
                UID:%s
                DTSTAMP:%s
                DTSTART;VALUE=DATE:%s
                DTEND;VALUE=DATE:%s
                SUMMARY:Soggiorno Hostpitami
                DESCRIPTION:Prenotazione confermata
                END:VEVENT
                END:VCALENDAR
                """).formatted(b.getId(), dtstamp, dtStart, dtEnd);
    }

    private int computeUpsellAmount(List<UpsellItemRequest> upsells, int nights) {
        if (upsells == null || upsells.isEmpty()) return 0;
        int total = 0;
        for (var u : upsells) {
            if ("BREAKFAST".equalsIgnoreCase(u.code())) total += 1200 * u.qty() * nights;
            if ("LATE_CHECKOUT".equalsIgnoreCase(u.code())) total += 2500 * u.qty();
        }
        return total;
    }

    private RatePlan pickPlan(UUID structureId, UUID roomId, UUID requestedPlanId) {
        if (requestedPlanId != null) {
            return ratePlans.findByIdAndStructureId(requestedPlanId, structureId)
                    .orElseThrow(() -> new IllegalArgumentException("RatePlan not found"));
        }

        List<RatePlan> active = ratePlans.findByStructureIdAndActiveTrueOrderByCreatedAtAsc(structureId);

        for (RatePlan p : active) {
            if (p.isAppliesToAllRooms()) return p;
            if (planRooms.existsByRatePlanIdAndRoomId(p.getId(), roomId)) return p;
        }

        throw new IllegalArgumentException("No active RatePlan for room");
    }

    private int countUsedUnits(UUID roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> list = bookings.findByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                roomId, checkOut, checkIn
        );

        int used = 0;
        for (Booking b : list) {
            if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.EXPIRED) continue;
            if (b.getStatus() == BookingStatus.HOLD &&
                    b.getHoldExpiresAt() != null &&
                    b.getHoldExpiresAt().isBefore(Instant.now())) continue;
            used++;
        }
        return used;
    }
}