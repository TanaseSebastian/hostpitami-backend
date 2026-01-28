package com.hostpitami.api.room.controller;

import com.hostpitami.api.room.dto.RoomRequest;
import com.hostpitami.api.room.dto.RoomResponse;
import com.hostpitami.common.web.TenantHeader;
import com.hostpitami.domain.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomResponse create(
            @RequestHeader(TenantHeader.TENANT_ID) UUID tenantId,
            @Valid @RequestBody RoomRequest req
    ) {
        return roomService.create(tenantId, req);
    }

    @GetMapping
    public List<RoomResponse> list(@RequestHeader(TenantHeader.TENANT_ID) UUID tenantId) {
        return roomService.list(tenantId);
    }

    @GetMapping("/{id}")
    public RoomResponse get(
            @RequestHeader(TenantHeader.TENANT_ID) UUID tenantId,
            @PathVariable UUID id
    ) {
        return roomService.get(tenantId, id);
    }

    @PutMapping("/{id}")
    public RoomResponse update(
            @RequestHeader(TenantHeader.TENANT_ID) UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody RoomRequest req
    ) {
        return roomService.update(tenantId, id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader(TenantHeader.TENANT_ID) UUID tenantId,
            @PathVariable UUID id
    ) {
        roomService.delete(tenantId, id);
    }
}
