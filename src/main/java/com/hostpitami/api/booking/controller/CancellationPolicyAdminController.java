package com.hostpitami.api.booking.controller;

import com.hostpitami.api.booking.dto.CancellationPolicyResponse;
import com.hostpitami.api.booking.dto.UpdateCancellationPolicyRequest;
import com.hostpitami.security.auth.AppUserDetails;
import com.hostpitami.security.auth.AuthResolver;
import com.hostpitami.service.booking.CancellationPolicyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/structures/{structureId}/cancellation-policy")
public class CancellationPolicyAdminController {

    private final CancellationPolicyService service;
    private final AuthResolver auth;

    public CancellationPolicyAdminController(
            CancellationPolicyService service,
            AuthResolver auth
    ) {
        this.service = service;
        this.auth = auth;
    }

    @GetMapping
    public CancellationPolicyResponse get(
            HttpServletRequest request,
            @PathVariable UUID structureId,
            @AuthenticationPrincipal AppUserDetails principal
    ) {
        UUID accountId = auth.accountId(request);
        return service.get(structureId, accountId);
    }

    @PutMapping
    public CancellationPolicyResponse update(
            HttpServletRequest request,
            @PathVariable UUID structureId,
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody UpdateCancellationPolicyRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return service.update(structureId, accountId, req);
    }
}