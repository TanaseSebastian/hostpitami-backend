package com.hostpitami.domain.entity.account;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "account_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_account_members_account_user", columnNames = {"account_id", "user_id"})
        },
        indexes = {
                @Index(name = "ix_account_members_account", columnList = "account_id"),
                @Index(name = "ix_account_members_user", columnList = "user_id")
        }
)
public class AccountMember {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 20)
    private AccountMemberRole role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AccountMember() {}

    public AccountMember(UUID accountId, UUID userId, AccountMemberRole role) {
        this.accountId = accountId;
        this.userId = userId;
        this.role = role;
        this.enabled = true;
    }

    // getters/setters
    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public AccountMemberRole getRole() { return role; }
    public void setRole(AccountMemberRole role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Instant getCreatedAt() { return createdAt; }
}
