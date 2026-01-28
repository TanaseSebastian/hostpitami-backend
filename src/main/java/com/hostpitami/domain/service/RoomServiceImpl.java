package com.hostpitami.domain.service;

import com.hostpitami.api.room.dto.RoomRequest;
import com.hostpitami.api.room.dto.RoomResponse;
import com.hostpitami.api.room.dto.RoomRequest;
import com.hostpitami.common.web.BadRequestException;
import com.hostpitami.common.web.NotFoundException;
import com.hostpitami.domain.entity.room.Room;
import com.hostpitami.domain.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse create(UUID tenantId, RoomRequest req) {

        roomRepository.findByTenantIdAndCode(tenantId, req.code())
                .ifPresent(r -> { throw new BadRequestException("Room code already exists for this tenant: " + req.code()); });

        Room room = new Room();
        room.setTenantId(tenantId);
        room.setCode(req.code());
        room.setName(req.name());
        room.setType(req.type());
        room.setMaxGuests(req.maxGuests());
        room.setBasePricePerNight(req.basePricePerNight());
        room.setActive(req.active() == null || req.active());

        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> list(UUID tenantId) {
        return roomRepository.findByTenantId(tenantId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse get(UUID tenantId, UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        if (!tenantId.equals(room.getTenantId())) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        return toResponse(room);
    }

    @Override
    public RoomResponse update(UUID tenantId, UUID roomId, RoomRequest req) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        if (!tenantId.equals(room.getTenantId())) {
            throw new NotFoundException("Room not found: " + roomId);
        }

        if (req.code() != null && !req.code().equals(room.getCode())) {
            roomRepository.findByTenantIdAndCode(tenantId, req.code())
                    .ifPresent(r -> { throw new BadRequestException("Room code already exists for this tenant: " + req.code()); });
            room.setCode(req.code());
        }

        if (req.name() != null) room.setName(req.name());
        if (req.type() != null) room.setType(req.type());
        if (req.maxGuests() > 0) room.setMaxGuests(req.maxGuests());
        if (req.basePricePerNight() != null) room.setBasePricePerNight(req.basePricePerNight());
        if (req.active() != null) room.setActive(req.active());

        return toResponse(room);
    }

    @Override
    public void delete(UUID tenantId, UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        if (!tenantId.equals(room.getTenantId())) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        roomRepository.delete(room);
    }

    private RoomResponse toResponse(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getTenantId(),
                r.getCode(),
                r.getName(),
                r.getType(),
                r.getMaxGuests(),
                r.getBasePricePerNight(),
                r.isActive(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
