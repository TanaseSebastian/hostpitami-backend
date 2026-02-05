package com.hostpitami.api.structure.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SetDomainRequest(

        // Es: "bnb-mario.it" oppure "booking.bnb-mario.it"
        @Size(max = 120)
        @Pattern(
                regexp = "^(?=.{1,120}$)(?!-)(?:[a-zA-Z0-9-]{1,63}\\.)+[a-zA-Z]{2,63}$",
                message = "Dominio non valido"
        )
        String domain,

        // Se true: la struttura userà il dominio impostato
        Boolean useCustomDomain,

        // Facoltativo: redirect automatico verso www.
        Boolean redirectToWww

) {}