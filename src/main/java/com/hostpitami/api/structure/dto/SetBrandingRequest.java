package com.hostpitami.api.structure.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SetBrandingRequest(

        // Branding
        @Size(max = 200) String logoUrl,
        @Size(max = 200) String coverImageUrl,

        // Template sito
        @Size(max = 80) String websiteTemplate,  // es: "minimal", "classic", "lux"

        // Colori (HEX)
        @Size(max = 20)
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "primaryColor deve essere un HEX valido")
        String primaryColor,

        @Size(max = 20)
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "accentColor deve essere un HEX valido")
        String accentColor,

        // SEO
        @Size(max = 70) String seoTitle,
        @Size(max = 160) String seoDescription

) {}
