package com.hostpitami.domain.entity.room;

import com.hostpitami.domain.entity.base.BaseEntity;
import com.hostpitami.domain.entity.structure.Structure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(name="ix_rooms_structure_id", columnList="structure_id"),
                @Index(name="ux_rooms_structure_slug", columnList="structure_id, slug", unique = true)
        }
)
public class Room extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RoomType type;

    @Column(nullable = false, length = 120)
    private String slug;

    // Inventory: quante camere fisiche identiche esistono
    @Column(nullable = false)
    private int quantity = 1;

    // Capacità
    @Column(nullable = false)
    private int maxAdults = 2;

    @Column(nullable = false)
    private int maxChildren = 0;

    // Informazioni descrittive
    @Column(length = 2000)
    private String description;

    @Column(length = 120)
    private String bedInfo; // es: "1 matrimoniale + 1 divano letto"

    @Column
    private Integer sizeMq; // metri quadri

    @Column(length = 2000)
    private String amenities; // MVP: stringa (es: "wifi,aria_condizionata,parcheggio")

    // Stato
    @Column(nullable = false)
    private boolean archived = false;

    // Immagine cover veloce (MVP)
    @Column(length = 300)
    private String coverImageUrl;
}