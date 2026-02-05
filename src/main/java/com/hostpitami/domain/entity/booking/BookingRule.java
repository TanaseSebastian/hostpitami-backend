package com.hostpitami.domain.entity.booking;

import com.hostpitami.domain.entity.structure.Structure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booking_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_booking_rule_structure",
                        columnNames = {"structure_id"}
                )
        })
public class BookingRule {

    @Id
    @GeneratedValue
    private java.util.UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    // --- soggiorno
    @Column(nullable = false)
    private int minNights = 1;

    @Column(nullable = false)
    private int maxNights = 30;

    // --- anticipo prenotazione
    @Column(nullable = false)
    private int minAdvanceDays = 0;     // oggi stesso

    @Column(nullable = false)
    private int maxAdvanceDays = 365;   // 1 anno

    // --- check-in consentiti
    @Column(nullable = false)
    private boolean allowCheckInMonday = true;
    private boolean allowCheckInTuesday = true;
    private boolean allowCheckInWednesday = true;
    private boolean allowCheckInThursday = true;
    private boolean allowCheckInFriday = true;
    private boolean allowCheckInSaturday = true;
    private boolean allowCheckInSunday = true;

    // --- check-in / check-out orari
    @Column(length = 5)
    private String checkInFrom;   // "15:00"

    @Column(length = 5)
    private String checkInTo;     // "20:00"

    @Column(length = 5)
    private String checkOutBy;    // "10:00"
}