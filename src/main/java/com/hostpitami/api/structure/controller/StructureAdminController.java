package com.hostpitami.api.structure.controller;

import com.hostpitami.api.structure.dto.*;
import com.hostpitami.security.auth.AppUserDetails;
import com.hostpitami.security.auth.AuthResolver;
import com.hostpitami.service.structure.StructureService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/structures")
public class StructureAdminController {

    private final StructureService structureService;
    private final AuthResolver auth;

    public StructureAdminController(StructureService structureService, AuthResolver auth) {
        this.structureService = structureService;
        this.auth = auth;
    }

    @GetMapping
    public List<StructureListItemResponse> list(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.list(accountId);
    }

    @PostMapping
    public ResponseEntity<StructureDetailResponse> create(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody CreateStructureRequest req
    ) {
        UUID accountId = auth.accountId(request);
        UUID userId = auth.userId(principal);

        var created = structureService.create(accountId, userId, req);

        return ResponseEntity
                .created(URI.create("/api/admin/structures/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public StructureDetailResponse get(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.get(accountId, id);
    }

    @PatchMapping("/{id}")
    public StructureDetailResponse update(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStructureRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.update(accountId, id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard
    ) {
        UUID accountId = auth.accountId(request);
        structureService.delete(accountId, id, hard);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public StructureDetailResponse publish(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.publish(accountId, id);
    }

    @PostMapping("/{id}/unpublish")
    public StructureDetailResponse unpublish(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.unpublish(accountId, id);
    }

    @PostMapping("/{id}/archive")
    public StructureDetailResponse archive(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.archive(accountId, id);
    }

    @PostMapping("/{id}/restore")
    public StructureDetailResponse restore(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.restore(accountId, id);
    }

    @PostMapping("/{id}/duplicate")
    public StructureDetailResponse duplicate(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id,
            @Valid @RequestBody DuplicateStructureRequest req
    ) {
        UUID accountId = auth.accountId(request);
        UUID userId = auth.userId(principal);
        return structureService.duplicate(accountId, userId, id, req);
    }

    @GetMapping("/{id}/website/preview")
    public WebsitePreviewResponse previewWebsite(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.previewWebsite(accountId, id);
    }

    @PostMapping("/{id}/website/regenerate")
    public WebsiteGenerationResponse regenerateWebsite(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID id
    ) {
        UUID accountId = auth.accountId(request);
        return structureService.regenerateWebsite(accountId, id);
    }
}