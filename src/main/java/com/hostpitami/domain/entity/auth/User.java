package com.hostpitami.domain.entity.auth;

import com.hostpitami.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "ix_users_email", columnList = "email", unique = true)
        })
public class User extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column()
    private String passwordHash;

    private String phone;

    private boolean isActive;

    @Column(nullable = false)
    private boolean enabled = true;
}