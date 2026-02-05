package com.hostpitami.api.structure.dto;

import com.hostpitami.domain.entity.structure.StructureType;
import jakarta.validation.constraints.*;

public record UpdateStructureRequest(

        @Size(max = 160)
        String name,

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

        @Size(max = 50)
        String timezone,

        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve essere ISO-4217 (es: EUR)")
        String currency,

        // orari "HH:mm"
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "checkInTime deve essere HH:mm")
        @Size(max = 10)
        String checkInTime,

        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "checkOutTime deve essere HH:mm")
        @Size(max = 10)
        String checkOutTime,

        // --- Website / Branding
        @Size(max = 120)
        String domain, // es: bnb-mario.it (solo dominio, no https://)

        @Size(max = 200)
        String logoUrl,

        @Size(max = 200)
        String coverImageUrl,

        // --- Tema / Template
        @Size(max = 80)
        String websiteTemplate, // es: "classic", "minimal", "lux"

        @Pattern(regexp = "^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$", message = "primaryColor deve essere HEX (#RRGGBB o #RRGGBBAA)")
        @Size(max = 20)
        String primaryColor,

        @Pattern(regexp = "^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$", message = "accentColor deve essere HEX (#RRGGBB o #RRGGBBAA)")
        @Size(max = 20)
        String accentColor,

        // --- SEO
        @Size(max = 70)
        String seoTitle,

        @Size(max = 160)
        String seoDescription

) {}