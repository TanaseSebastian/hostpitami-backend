package com.hostpitami.service.room;

import com.hostpitami.api.room.dto.*;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    List<RoomListItemResponse> list(UUID accountId, UUID structureId);

    RoomDetailResponse create(UUID accountId, UUID structureId, CreateRoomRequest req);

    RoomDetailResponse get(UUID accountId, UUID structureId, UUID roomId);

    RoomDetailResponse update(UUID accountId, UUID structureId, UUID roomId, UpdateRoomRequest req);

    void delete(UUID accountId, UUID structureId, UUID roomId, boolean hard);

    RoomDetailResponse archive(UUID accountId, UUID structureId, UUID roomId);

    RoomDetailResponse restore(UUID accountId, UUID structureId, UUID roomId);
}