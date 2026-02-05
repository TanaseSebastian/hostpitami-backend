package com.hostpitami.domain.entity.rates;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@Entity
@Table(name="rate_plan_rooms",
        uniqueConstraints = @UniqueConstraint(
                name="ux_rate_plan_rooms",
                columnNames = {"rate_plan_id","room_id"}
        )
)
@IdClass(RatePlanRoom.Key.class)
public class RatePlanRoom {

    @Id
    @Column(name="rate_plan_id", nullable = false)
    private UUID ratePlanId;

    @Id
    @Column(name="room_id", nullable = false)
    private UUID roomId;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Key implements Serializable {
        private UUID ratePlanId;
        private UUID roomId;
    }
}