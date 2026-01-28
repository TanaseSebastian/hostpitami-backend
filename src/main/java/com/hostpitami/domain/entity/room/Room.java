package com.hostpitami.domain.entity.room;

import com.hostpitami.domain.entity.base.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rooms_tenant_code", columnNames = {"tenant_id", "code"})
        },
        indexes = {
                @Index(name = "ix_rooms_tenant", columnList = "tenant_id")
        })
public class Room extends TenantEntity {

    @Column(nullable = false, length = 30)
    private String code; // es: "CAM1", "A1", "101"

    @Column(nullable = false, length = 160)
    private String name; // es: "Camera Matrimoniale"

    @Column(length = 240)
    private String shortDescription;

    @Column(columnDefinition = "text")
    private String longDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomType type;

    @Column(nullable = false)
    private int maxGuests;

    @Column(precision = 10, scale = 2)
    private BigDecimal basePricePerNight;

    @Column(nullable = false)
    private boolean active = true;
}