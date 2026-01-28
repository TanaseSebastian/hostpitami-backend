package com.hostpitami.domain.entity.auth;

import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
        },
        indexes = {
                @Index(name = "ix_user_roles_user", columnList = "user_id"),
                @Index(name = "ix_user_roles_role", columnList = "role_id")
        })
public class UserRole extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}