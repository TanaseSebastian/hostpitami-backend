package com.hostpitami.domain.service;

import com.hostpitami.api.room.dto.RoomRequest;
import com.hostpitami.api.room.dto.RoomResponse;
import com.hostpitami.api.room.dto.RoomRequest;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponse create(UUID tenantId, RoomRequest req);
    List<RoomResponse> list(UUID tenantId);
    RoomResponse get(UUID tenantId, UUID roomId);
    RoomResponse update(UUID tenantId, UUID roomId, RoomRequest req);
    void delete(UUID tenantId, UUID roomId);
}
