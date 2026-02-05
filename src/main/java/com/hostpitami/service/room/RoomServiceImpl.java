package com.hostpitami.service.room;

import com.hostpitami.api.room.dto.*;
import com.hostpitami.domain.entity.room.Room;
import com.hostpitami.domain.entity.room.RoomType;
import com.hostpitami.domain.repository.room.RoomRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository rooms;
    private final StructureRepository structures;

    public RoomServiceImpl(RoomRepository rooms, StructureRepository structures) {
        this.rooms = rooms;
        this.structures = structures;
    }

    @Override
    public List<RoomListItemResponse> list(UUID accountId, UUID structureId) {
        // check structure belongs to account
        structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        return rooms.findByStructureIdAndStructureAccountId(structureId, accountId)
                .stream()
                .map(this::toListItem)
                .toList();
    }

    @Override
    @Transactional
    public RoomDetailResponse create(UUID accountId, UUID structureId, CreateRoomRequest req) {
        var structure = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        Room r = new Room();
        r.setStructure(structure);

        r.setName(req.name().trim());
        r.setType(parseType(req.type()));

        r.setQuantity(req.quantity() != null ? req.quantity() : 1);
        r.setMaxAdults(req.maxAdults() != null ? req.maxAdults() : 2);
        r.setMaxChildren(req.maxChildren() != null ? req.maxChildren() : 0);

        r.setDescription(trimOrNull(req.description()));
        r.setBedInfo(trimOrNull(req.bedInfo()));
        r.setSizeMq(req.sizeMq());
        r.setAmenities(trimOrNull(req.amenities()));
        r.setCoverImageUrl(trimOrNull(req.coverImageUrl()));

        String desiredSlug = isBlank(req.slug()) ? slugify(req.name()) : slugify(req.slug());
        r.setSlug(ensureUniqueSlug(accountId, structureId, desiredSlug, null));

        Room saved = rooms.save(r);
        return toDetail(saved);
    }

    @Override
    public RoomDetailResponse get(UUID accountId, UUID structureId, UUID roomId) {
        Room r = rooms.findByIdAndStructureIdAndStructureAccountId(roomId, structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return toDetail(r);
    }

    @Override
    @Transactional
    public RoomDetailResponse update(UUID accountId, UUID structureId, UUID roomId, UpdateRoomRequest req) {
        Room r = rooms.findByIdAndStructureIdAndStructureAccountId(roomId, structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (req.name() != null && !req.name().isBlank()) r.setName(req.name().trim());
        if (req.type() != null && !req.type().isBlank()) r.setType(parseType(req.type()));

        if (req.quantity() != null) r.setQuantity(req.quantity());
        if (req.maxAdults() != null) r.setMaxAdults(req.maxAdults());
        if (req.maxChildren() != null) r.setMaxChildren(req.maxChildren());

        if (req.description() != null) r.setDescription(trimOrNull(req.description()));
        if (req.bedInfo() != null) r.setBedInfo(trimOrNull(req.bedInfo()));
        if (req.sizeMq() != null) r.setSizeMq(req.sizeMq());
        if (req.amenities() != null) r.setAmenities(trimOrNull(req.amenities()));
        if (req.coverImageUrl() != null) r.setCoverImageUrl(trimOrNull(req.coverImageUrl()));

        if (req.slug() != null && !req.slug().isBlank()) {
            String desired = slugify(req.slug());
            r.setSlug(ensureUniqueSlug(accountId, structureId, desired, r.getId()));
        }

        return toDetail(rooms.save(r));
    }

    @Override
    @Transactional
    public void delete(UUID accountId, UUID structureId, UUID roomId, boolean hard) {
        Room r = rooms.findByIdAndStructureIdAndStructureAccountId(roomId, structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (hard) {
            rooms.delete(r);
            return;
        }

        // soft delete = archive
        r.setArchived(true);
        rooms.save(r);
    }

    @Override
    @Transactional
    public RoomDetailResponse archive(UUID accountId, UUID structureId, UUID roomId) {
        Room r = rooms.findByIdAndStructureIdAndStructureAccountId(roomId, structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        r.setArchived(true);
        return toDetail(rooms.save(r));
    }

    @Override
    @Transactional
    public RoomDetailResponse restore(UUID accountId, UUID structureId, UUID roomId) {
        Room r = rooms.findByIdAndStructureIdAndStructureAccountId(roomId, structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        r.setArchived(false);
        return toDetail(rooms.save(r));
    }

    // ---------------- helpers ----------------

    private RoomType parseType(String raw) {
        try {
            return RoomType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid room type: " + raw);
        }
    }

    private String ensureUniqueSlug(UUID accountId, UUID structureId, String desired, UUID excludeId) {
        String base = desired;
        String slug = base;
        int i = 2;

        if (excludeId == null) {
            while (rooms.existsByStructureIdAndStructureAccountIdAndSlug(structureId, accountId, slug)) {
                slug = base + "-" + i++;
            }
        } else {
            while (rooms.existsByStructureIdAndStructureAccountIdAndSlugAndIdNot(structureId, accountId, slug, excludeId)) {
                slug = base + "-" + i++;
            }
        }
        return slug;
    }

    private String slugify(String input) {
        String s = (input == null ? "" : input).trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9\\s-]", "");
        s = s.replaceAll("\\s+", "-");
        s = s.replaceAll("-{2,}", "-");
        s = s.replaceAll("^-|-$", "");
        return s.isBlank() ? "room" : s;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private OffsetDateTime toOffset(Instant i) {
        return i == null ? null : i.atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime();
    }

    private RoomListItemResponse toListItem(Room r) {
        return new RoomListItemResponse(
                r.getId(),
                r.getStructure().getId(),
                r.getName(),
                r.getType().name(),
                r.getSlug(),
                r.getQuantity(),
                r.getMaxAdults(),
                r.getMaxChildren(),
                r.isArchived(),
                r.getCoverImageUrl(),
                toOffset(r.getCreatedAt()),
                toOffset(r.getUpdatedAt())
        );
    }

    private RoomDetailResponse toDetail(Room r) {
        return new RoomDetailResponse(
                r.getId(),
                r.getStructure().getId(),
                r.getName(),
                r.getType().name(),
                r.getSlug(),
                r.getQuantity(),
                r.getMaxAdults(),
                r.getMaxChildren(),
                r.getDescription(),
                r.getBedInfo(),
                r.getSizeMq(),
                r.getAmenities(),
                r.isArchived(),
                r.getCoverImageUrl(),
                toOffset(r.getCreatedAt()),
                toOffset(r.getUpdatedAt())
        );
    }
}