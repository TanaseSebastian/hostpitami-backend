package com.hostpitami.domain.entity.account;

import com.hostpitami.domain.entity.auth.User;
import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "accounts",
        indexes = {
                @Index(name = "ix_accounts_owner_id", columnList = "owner_user_id")
        })
public class Account extends BaseEntity {

    // Owner dell'account (cliente pagante / admin principale)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AccountType type = AccountType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    private PlanType plan = PlanType.FREE;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_status", nullable = false, length = 20)
    private PlanStatus planStatus = PlanStatus.NONE;

    @Column(name = "trial_ends_at")
    private Instant trialEndsAt;
}