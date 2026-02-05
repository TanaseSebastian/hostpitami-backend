package com.hostpitami.domain.entity.rates;

import com.hostpitami.domain.entity.structure.Structure;
import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="rate_plans",
        indexes = {
                @Index(name="ix_rate_plans_structure_id", columnList="structure_id")
        }
)
public class RatePlan extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="structure_id", nullable = false)
    private Structure structure;

    @Column(nullable = false, length = 120)
    private String name; // "Standard", "Non rimborsabile"

    @Column(nullable = false, length = 40)
    private String code; // STANDARD, NON_REFUNDABLE, WEEKLY

    @Column(nullable = false)
    private boolean active = true;

    // Se true: si applica a tutte le camere (senza tabella di mapping)
    @Column(nullable = false)
    private boolean appliesToAllRooms = true;

    // Pricing base
    @Column(nullable = false)
    private int basePriceCents = 0;

    @Column(nullable = false)
    private int defaultMinStay = 1;

    // Differenza prezzo rispetto allo standard: es -10% su non rimborsabile
    @Column(nullable = false)
    private int discountPercent = 0; // 0..100
}