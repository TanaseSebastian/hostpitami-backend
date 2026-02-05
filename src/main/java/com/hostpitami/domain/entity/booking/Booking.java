package com.hostpitami.domain.entity.booking;

import com.hostpitami.domain.entity.base.TenantEntity;
import com.hostpitami.domain.entity.guest.Guest;
import com.hostpitami.domain.entity.room.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "ix_bookings_tenant", columnList = "tenant_id"),
                @Index(name = "ix_bookings_room", columnList = "room_id"),
                @Index(name = "ix_bookings_guest", columnList = "guest_id")
        })
public class Booking extends TenantEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private int guestsCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(length = 500)
    private String notes;

    @Column
    private Instant holdExpiresAt;

    // token pubblico per link voucher/ics senza login
    @Column(length = 80, unique = true)
    private String publicToken;

    @Column(length = 30)
    private String paymentStatus; // NONE, PENDING, PAID

    @Column(length = 80)
    private String guestFirstName;

    @Column(length = 80)
    private String guestLastName;

    @Column(length = 120)
    private String guestEmail;

    @Column(length = 30)
    private String guestPhone;
}