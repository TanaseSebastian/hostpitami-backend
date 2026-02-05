package com.hostpitami.domain.entity.rates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@Entity
@Table(name="rate_calendar",
        uniqueConstraints = @UniqueConstraint(
                name="ux_rate_calendar",
                columnNames = {"rate_plan_id","room_id","date"}
        ),
        indexes = {
                @Index(name="ix_rate_calendar_plan_room_date", columnList="rate_plan_id,room_id,date")
        }
)
public class RateCalendar {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="rate_plan_id", nullable = false)
    private UUID ratePlanId;

    @Column(name="room_id", nullable = false)
    private UUID roomId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int priceCents;

    @Column(nullable = false)
    private int minStay = 1;

    @Column(nullable = false)
    private boolean closed = false; // stop-sell / camera chiusa in quel giorno
}