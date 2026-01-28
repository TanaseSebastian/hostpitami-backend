package com.hostpitami.domain.entity.structure;

import com.hostpitami.domain.entity.account.Account;
import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="structures",
        indexes = {
                @Index(name="ix_structures_account_id", columnList="account_id"),
                @Index(name="ux_structures_slug", columnList="slug", unique=true)
        }
)
public class Structure extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StructureType type;

    @Column(nullable = false, length = 120, unique = true)
    private String slug;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Dati base
    @Column(length = 120)
    private String phone;

    @Column(length = 180)
    private String email;

    @Column(length = 220)
    private String addressLine;

    @Column(length = 80)
    private String city;

    @Column(length = 30)
    private String postalCode;

    @Column(length = 80)
    private String country;

    @Column(nullable = false, length = 50)
    private String timezone = "Europe/Rome";

    @Column(nullable = false, length = 3)
    private String currency = "EUR";

    @Column(length = 10)
    private String checkInTime;   // "15:00"
    @Column(length = 10)
    private String checkOutTime;
}