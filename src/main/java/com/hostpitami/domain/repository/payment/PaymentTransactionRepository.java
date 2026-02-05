package com.hostpitami.domain.repository.payment;

import com.hostpitami.domain.entity.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByStripePaymentIntentId(String intentId);
    Optional<PaymentTransaction> findByBookingId(UUID bookingId);
}