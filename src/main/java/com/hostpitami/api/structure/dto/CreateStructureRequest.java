package com.hostpitami.api.structure.dto;

import com.hostpitami.domain.entity.structure.StructureType;
import jakarta.validation.constraints.*;

public record CreateStructureRequest(
        @NotBlank
        @Size(max = 160)
        String name,
        @NotNull
        StructureType type,
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "slug non valido (usa lettere minuscole, numeri e trattini)")
        @Size(max = 120)
        String slug,
        @Size(max = 120)
        String phone,
        @Email
        @Size(max = 180)
        String email,
        @Size(max = 220)
        String addressLine,
        @Size(max = 80)
        String city,
        @Size(max = 30)
        String postalCode,
        @Size(max = 80)
        String country,
        // default Europe/Rome
        @Size(max = 50)
        String timezone,
        // default EUR
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve essere ISO-4217 (es: EUR)")
        String currency
) {}