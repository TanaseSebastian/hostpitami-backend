package com.hostpitami.api.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuestRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Email @Size(max = 160) String email,
        @Size(max = 30) String phone,
        @Size(max = 2) String countryCode,      // es. "IT"
        @Size(max = 50) String documentType,    // es. "ID_CARD"
        @Size(max = 50) String documentNumber,  // es. "CA123456"
        @Size(max = 500) String notes,
        Boolean active
) {}
