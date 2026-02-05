package com.hostpitami.domain.entity.booking;

import com.hostpitami.domain.entity.structure.Structure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "cancellation_policies",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_policy_structure", columnNames = "structure_id")
        }
)
public class CancellationPolicy {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    /**
     * STANDARD | NON_REFUNDABLE | CUSTOM
     */
    @Column(nullable = false, length = 30)
    private String type = "STANDARD";

    /**
     * Giorni prima del check-in entro cui è possibile cancellare gratuitamente.
     * Esempio: 7 = fino a 7 giorni prima.
     */
    private Integer freeCancellationDays;

    /**
     * Percentuale trattenuta in caso di cancellazione tardiva.
     * Esempio: 100 = nessun rimborso
     */
    private Integer penaltyPercentage;

    /**
     * Testo libero mostrato al cliente (frontend + email).
     */
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}