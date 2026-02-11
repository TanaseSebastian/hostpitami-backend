package com.hostpitami.domain.entity.guest;

import com.hostpitami.domain.entity.base.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "guests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_guests_tenant_email",
                        columnNames = {"tenant_id", "email"}
                )
        },
        indexes = {
                @Index(name = "ix_guests_tenant", columnList = "tenant_id"),
                @Index(name = "ix_guests_tenant_email", columnList = "tenant_id,email"),
                @Index(name = "ix_guests_tenant_last_name", columnList = "tenant_id,last_name")
        }
)
public class Guest extends TenantEntity {

    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @Column(name = "email", length = 180)
    private String email;

    @Column(name = "phone", length = 40)
    private String phone;

    @Column(name = "country_code", length = 2)
    private String countryCode; // es: "IT"

    @Column(name = "document_type", length = 60)
    private String documentType; // es: "ID_CARD", "PASSPORT"

    @Column(name = "document_number", length = 60)
    private String documentNumber; // CI/passaporto

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
