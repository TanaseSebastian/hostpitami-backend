package com.hostpitami.service.booking;

import com.hostpitami.api.booking.dto.BookingRuleResponse;
import com.hostpitami.api.booking.dto.UpdateBookingRuleRequest;
import com.hostpitami.domain.entity.booking.BookingRule;
import com.hostpitami.domain.entity.structure.Structure;
import com.hostpitami.domain.repository.booking.BookingRuleRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class BookingRuleServiceImpl implements BookingRuleService {

    private final BookingRuleRepository rules;
    private final StructureRepository structures;

    public BookingRuleServiceImpl(
            BookingRuleRepository rules,
            StructureRepository structures
    ) {
        this.rules = rules;
        this.structures = structures;
    }

    /**
     * Restituisce le regole di prenotazione della struttura.
     * Se non esistono, le crea con valori di default (lazy init).
     */
    @Override
    public BookingRuleResponse get(UUID structureId, UUID accountId) {
        BookingRule rule = rules.findByStructureId(structureId)
                .orElseGet(() -> createDefault(structureId, accountId));

        return toResponse(rule);
    }

    /**
     * Aggiorna le regole di prenotazione della struttura.
     */
    @Override
    public BookingRuleResponse update(
            UUID structureId,
            UUID accountId,
            UpdateBookingRuleRequest req
    ) {
        BookingRule rule = rules.findByStructureId(structureId)
                .orElseThrow(() -> new IllegalArgumentException("Booking rules not found"));

        if (req.minNights() != null) rule.setMinNights(req.minNights());
        if (req.maxNights() != null) rule.setMaxNights(req.maxNights());

        if (req.minAdvanceDays() != null) rule.setMinAdvanceDays(req.minAdvanceDays());
        if (req.maxAdvanceDays() != null) rule.setMaxAdvanceDays(req.maxAdvanceDays());

        if (req.allowCheckInMonday() != null) rule.setAllowCheckInMonday(req.allowCheckInMonday());
        if (req.allowCheckInTuesday() != null) rule.setAllowCheckInTuesday(req.allowCheckInTuesday());
        if (req.allowCheckInWednesday() != null) rule.setAllowCheckInWednesday(req.allowCheckInWednesday());
        if (req.allowCheckInThursday() != null) rule.setAllowCheckInThursday(req.allowCheckInThursday());
        if (req.allowCheckInFriday() != null) rule.setAllowCheckInFriday(req.allowCheckInFriday());
        if (req.allowCheckInSaturday() != null) rule.setAllowCheckInSaturday(req.allowCheckInSaturday());
        if (req.allowCheckInSunday() != null) rule.setAllowCheckInSunday(req.allowCheckInSunday());

        if (req.checkInFrom() != null) rule.setCheckInFrom(req.checkInFrom());
        if (req.checkInTo() != null) rule.setCheckInTo(req.checkInTo());
        if (req.checkOutBy() != null) rule.setCheckOutBy(req.checkOutBy());

        return toResponse(rule);
    }

    /**
     * Crea regole di default per una struttura (una sola volta).
     */
    private BookingRule createDefault(UUID structureId, UUID accountId) {
        Structure structure = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        BookingRule rule = new BookingRule();
        rule.setStructure(structure);

        // I default sono già definiti nell'entity (min 1 notte, ecc.)
        return rules.save(rule);
    }

    /**
     * Mapping entity -> DTO
     */
    private BookingRuleResponse toResponse(BookingRule r) {
        return new BookingRuleResponse(
                r.getMinNights(),
                r.getMaxNights(),
                r.getMinAdvanceDays(),
                r.getMaxAdvanceDays(),

                r.isAllowCheckInMonday(),
                r.isAllowCheckInTuesday(),
                r.isAllowCheckInWednesday(),
                r.isAllowCheckInThursday(),
                r.isAllowCheckInFriday(),
                r.isAllowCheckInSaturday(),
                r.isAllowCheckInSunday(),

                r.getCheckInFrom(),
                r.getCheckInTo(),
                r.getCheckOutBy()
        );
    }
}