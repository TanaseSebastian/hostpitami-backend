package com.hostpitami.api.room.controller;

import com.hostpitami.api.room.dto.*;
import com.hostpitami.security.auth.AppUserDetails;
import com.hostpitami.security.auth.AuthResolver;
import com.hostpitami.service.room.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/structures/{structureId}/rooms")
public class RoomAdminController {

    private final RoomService roomService;
    private final AuthResolver auth;

    public RoomAdminController(RoomService roomService, AuthResolver auth) {
        this.roomService = roomService;
        this.auth = auth;
    }

    @GetMapping
    public List<RoomListItemResponse> list(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId
    ) {
        UUID accountId = auth.accountId(request);
        return roomService.list(accountId, structureId);
    }

    @PostMapping
    public ResponseEntity<RoomDetailResponse> create(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @Valid @RequestBody CreateRoomRequest req
    ) {
        UUID accountId = auth.accountId(request);

        var created = roomService.create(accountId, structureId, req);

        return ResponseEntity
                .created(URI.create("/api/admin/structures/" + structureId + "/rooms/" + created.id()))
                .body(created);
    }

    @GetMapping("/{roomId}")
    public RoomDetailResponse get(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID roomId
    ) {
        UUID accountId = auth.accountId(request);
        return roomService.get(accountId, structureId, roomId);
    }

    @PatchMapping("/{roomId}")
    public RoomDetailResponse update(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomRequest req
    ) {
        UUID accountId = auth.accountId(request);
        return roomService.update(accountId, structureId, roomId, req);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID roomId,
            @RequestParam(name = "hard", defaultValue = "false") boolean hard
    ) {
        UUID accountId = auth.accountId(request);
        roomService.delete(accountId, structureId, roomId, hard);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/archive")
    public RoomDetailResponse archive(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID roomId
    ) {
        UUID accountId = auth.accountId(request);
        return roomService.archive(accountId, structureId, roomId);
    }

    @PostMapping("/{roomId}/restore")
    public RoomDetailResponse restore(
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable UUID structureId,
            @PathVariable UUID roomId
    ) {
        UUID accountId = auth.accountId(request);
        return roomService.restore(accountId, structureId, roomId);
    }
}