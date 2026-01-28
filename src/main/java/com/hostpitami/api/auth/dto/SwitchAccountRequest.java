package com.hostpitami.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SwitchAccountRequest(@NotBlank String accountId) {}
