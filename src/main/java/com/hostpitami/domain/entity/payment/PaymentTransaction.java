package com.hostpitami.domain.entity.payment;

import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "payment_transactions",
        indexes = {
                @Index(name="ix_pay_tx_booking", columnList="bookingId"),
                @Index(name="ix_pay_tx_intent", columnList="stripePaymentIntentId", unique = true)
        }
)
public class PaymentTransaction extends BaseEntity {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false, length = 120)
    private String stripePaymentIntentId;

    @Column(nullable = false)
    private int amountCents;

    @Column(nullable = false, length = 10)
    private String currency = "eur";

    @Column(nullable = false, length = 30)
    private String status; // CREATED, SUCCEEDED, FAILED, CANCELED
}
