package com.hostpitami.service.structure;

import com.hostpitami.api.structure.dto.*;
import com.hostpitami.domain.entity.account.Account;
import com.hostpitami.domain.entity.structure.Structure;
import com.hostpitami.domain.entity.structure.StructureType;
import com.hostpitami.domain.repository.account.AccountRepository;
import com.hostpitami.domain.repository.structure.StructureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class StructureServiceImpl implements StructureService {

    private final StructureRepository structures;
    private final AccountRepository accounts;

    public StructureServiceImpl(StructureRepository structures, AccountRepository accounts) {
        this.structures = structures;
        this.accounts = accounts;
    }

    @Override
    public List<StructureListItemResponse> list(UUID accountId) {
        return structures.findByAccountId(accountId).stream()
                .map(this::toListItem)
                .toList();
    }

    @Override
    @Transactional
    public StructureDetailResponse create(UUID accountId, UUID userId, CreateStructureRequest req) {
        Account account = accounts.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Structure s = new Structure();
        s.setAccount(account);

        s.setName(req.name().trim());
        s.setType(req.type());

        // defaults
        s.setTimezone(isBlank(req.timezone()) ? "Europe/Rome" : req.timezone().trim());
        s.setCurrency(isBlank(req.currency()) ? "EUR" : req.currency().trim().toUpperCase(Locale.ROOT));

        // contatti/indirizzo (opzionali)
        s.setPhone(trimOrNull(req.phone()));
        s.setEmail(trimOrNull(req.email()));
        s.setAddressLine(trimOrNull(req.addressLine()));
        s.setCity(trimOrNull(req.city()));
        s.setPostalCode(trimOrNull(req.postalCode()));
        s.setCountry(trimOrNull(req.country()));

        // slug: se non fornito -> generato
        String desiredSlug = isBlank(req.slug()) ? slugify(req.name()) : slugify(req.slug());
        s.setSlug(ensureUniqueSlug(accountId, desiredSlug, null));

        // sito
        s.setPublished(false);
        s.setArchived(false);
        s.setWebsiteStatus("DRAFT");

        Structure saved = structures.save(s);
        return toDetail(saved);
    }

    @Override
    public StructureDetailResponse get(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));
        return toDetail(s);
    }

    @Transactional
    @Override
    public StructureDetailResponse update(UUID accountId, UUID structureId, UpdateStructureRequest req) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        boolean websiteDirty = false; // se cambia qualcosa che impatta il sito -> DRAFT

        // --------- general ----------
        if (req.name() != null && !req.name().isBlank()) {
            s.setName(req.name().trim());
        }

        if (req.type() != null) {
            s.setType(req.type());
        }

        // slug (editabile)
        if (req.slug() != null) {
            String raw = req.slug().trim();
            if (raw.isBlank()) {
                // se vuoi permettere "svuota slug" -> rigeneralo da name
                // altrimenti commenta queste 3 righe
                String desired = slugify(s.getName());
                String unique = ensureUniqueSlug(accountId, desired, s.getId());
                s.setSlug(unique);
                websiteDirty = true;
            } else {
                String desired = slugify(raw);
                String unique = ensureUniqueSlug(accountId, desired, s.getId());
                if (!unique.equals(s.getSlug())) websiteDirty = true;
                s.setSlug(unique);
            }
        }

        // contatti/indirizzo
        if (req.phone() != null) s.setPhone(trimOrNull(req.phone()));
        if (req.email() != null) s.setEmail(trimOrNull(req.email()));
        if (req.addressLine() != null) s.setAddressLine(trimOrNull(req.addressLine()));
        if (req.city() != null) s.setCity(trimOrNull(req.city()));
        if (req.postalCode() != null) s.setPostalCode(trimOrNull(req.postalCode()));
        if (req.country() != null) s.setCountry(trimOrNull(req.country()));

        // timezone/currency
        if (req.timezone() != null && !req.timezone().isBlank()) s.setTimezone(req.timezone().trim());
        if (req.currency() != null && !req.currency().isBlank()) s.setCurrency(req.currency().trim().toUpperCase(Locale.ROOT));

        // checkin/out
        if (req.checkInTime() != null) s.setCheckInTime(trimOrNull(req.checkInTime()));
        if (req.checkOutTime() != null) s.setCheckOutTime(trimOrNull(req.checkOutTime()));

        // --------- website / branding / seo ----------
        if (req.domain() != null) {
            String newDomain = trimOrNull(req.domain());
            if ((newDomain == null && s.getDomain() != null) || (newDomain != null && !newDomain.equals(s.getDomain()))) {
                websiteDirty = true;
            }
            s.setDomain(newDomain);
        }

        if (req.logoUrl() != null) {
            String newVal = trimOrNull(req.logoUrl());
            if ((newVal == null && s.getLogoUrl() != null) || (newVal != null && !newVal.equals(s.getLogoUrl()))) {
                websiteDirty = true;
            }
            s.setLogoUrl(newVal);
        }

        if (req.coverImageUrl() != null) {
            String newVal = trimOrNull(req.coverImageUrl());
            if ((newVal == null && s.getCoverImageUrl() != null) || (newVal != null && !newVal.equals(s.getCoverImageUrl()))) {
                websiteDirty = true;
            }
            s.setCoverImageUrl(newVal);
        }

        if (req.websiteTemplate() != null) {
            String newVal = trimOrNull(req.websiteTemplate());
            if ((newVal == null && s.getWebsiteTemplate() != null) || (newVal != null && !newVal.equals(s.getWebsiteTemplate()))) {
                websiteDirty = true;
            }
            s.setWebsiteTemplate(newVal);
        }

        if (req.primaryColor() != null) {
            String newVal = trimOrNull(req.primaryColor());
            if ((newVal == null && s.getPrimaryColor() != null) || (newVal != null && !newVal.equals(s.getPrimaryColor()))) {
                websiteDirty = true;
            }
            s.setPrimaryColor(newVal);
        }

        if (req.accentColor() != null) {
            String newVal = trimOrNull(req.accentColor());
            if ((newVal == null && s.getAccentColor() != null) || (newVal != null && !newVal.equals(s.getAccentColor()))) {
                websiteDirty = true;
            }
            s.setAccentColor(newVal);
        }

        if (req.seoTitle() != null) {
            String newVal = trimOrNull(req.seoTitle());
            if ((newVal == null && s.getSeoTitle() != null) || (newVal != null && !newVal.equals(s.getSeoTitle()))) {
                websiteDirty = true;
            }
            s.setSeoTitle(newVal);
        }

        if (req.seoDescription() != null) {
            String newVal = trimOrNull(req.seoDescription());
            if ((newVal == null && s.getSeoDescription() != null) || (newVal != null && !newVal.equals(s.getSeoDescription()))) {
                websiteDirty = true;
            }
            s.setSeoDescription(newVal);
        }

        // se cambia qualcosa che impatta il sito -> torna DRAFT
        if (websiteDirty) {
            s.setWebsiteStatus("DRAFT");
        }

        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public void delete(UUID accountId, UUID structureId, boolean hard) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        if (hard) {
            structures.delete(s);
            return;
        }

        // soft delete = archive (scelta MVP)
        s.setArchived(true);
        s.setPublished(false);
        structures.save(s);
    }

    @Override
    @Transactional
    public StructureDetailResponse publish(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        if (s.isArchived()) throw new IllegalStateException("Structure archived");

        // regola MVP: puoi pubblicare anche senza dominio custom: usa /s/{slug}
        s.setPublished(true);
        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse unpublish(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));
        s.setPublished(false);
        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse archive(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        s.setArchived(true);
        s.setPublished(false);
        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse restore(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        s.setArchived(false);
        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse setDomain(UUID accountId, UUID structureId, SetDomainRequest req) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        // domain può essere null -> rimuovi dominio custom
        s.setDomain(trimOrNull(req.domain()));
        s.setUseCustomDomain(Boolean.TRUE.equals(req.useCustomDomain()) && !isBlank(req.domain()));
        s.setRedirectToWww(Boolean.TRUE.equals(req.redirectToWww()));

        // cambi dominio => sito da rigenerare
        s.setWebsiteStatus("DRAFT");

        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse setBranding(UUID accountId, UUID structureId, SetBrandingRequest req) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        if (req.logoUrl() != null) s.setLogoUrl(trimOrNull(req.logoUrl()));
        if (req.coverImageUrl() != null) s.setCoverImageUrl(trimOrNull(req.coverImageUrl()));

        if (req.websiteTemplate() != null) s.setWebsiteTemplate(trimOrNull(req.websiteTemplate()));

        if (req.primaryColor() != null) s.setPrimaryColor(trimOrNull(req.primaryColor()));
        if (req.accentColor() != null) s.setAccentColor(trimOrNull(req.accentColor()));

        if (req.seoTitle() != null) s.setSeoTitle(trimOrNull(req.seoTitle()));
        if (req.seoDescription() != null) s.setSeoDescription(trimOrNull(req.seoDescription()));

        s.setWebsiteStatus("DRAFT");
        return toDetail(structures.save(s));
    }

    @Override
    @Transactional
    public StructureDetailResponse duplicate(UUID accountId, UUID userId, UUID structureId, DuplicateStructureRequest req) {
        Structure src = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        Structure copy = new Structure();
        copy.setAccount(src.getAccount());

        copy.setName((src.getName() + " (copia)").trim());
        copy.setType(src.getType());

        copy.setPhone(src.getPhone());
        copy.setEmail(src.getEmail());
        copy.setAddressLine(src.getAddressLine());
        copy.setCity(src.getCity());
        copy.setPostalCode(src.getPostalCode());
        copy.setCountry(src.getCountry());
        copy.setTimezone(src.getTimezone());
        copy.setCurrency(src.getCurrency());
        copy.setCheckInTime(src.getCheckInTime());
        copy.setCheckOutTime(src.getCheckOutTime());

        // Branding/SEO/template
        copy.setLogoUrl(src.getLogoUrl());
        copy.setCoverImageUrl(src.getCoverImageUrl());
        copy.setWebsiteTemplate(src.getWebsiteTemplate());
        copy.setPrimaryColor(src.getPrimaryColor());
        copy.setAccentColor(src.getAccentColor());
        copy.setSeoTitle(src.getSeoTitle());
        copy.setSeoDescription(src.getSeoDescription());

        // dominio NON lo copio (eviti conflitti)
        copy.setDomain(null);
        copy.setUseCustomDomain(false);
        copy.setRedirectToWww(false);

        // slug unico
        String desiredSlug = slugify(src.getSlug() + "-copia");
        copy.setSlug(ensureUniqueSlug(accountId, desiredSlug, null));

        copy.setPublished(false);
        copy.setArchived(false);
        copy.setWebsiteStatus("DRAFT");

        Structure saved = structures.save(copy);

        // TODO futuro: se req.includeRooms() -> copia camere
        // TODO futuro: se req.includeRates() -> copia tariffe

        return toDetail(saved);
    }

    @Override
    public WebsitePreviewResponse previewWebsite(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        // Preview MVP: path basato su slug
        String previewUrl = "http://localhost:5173/s/" + s.getSlug() + "?preview=1";
        return new WebsitePreviewResponse(
                previewUrl,
                s.getWebsiteStatus()
        );
    }

    @Override
    @Transactional
    public WebsiteGenerationResponse regenerateWebsite(UUID accountId, UUID structureId) {
        Structure s = structures.findByIdAndAccountId(structureId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found"));

        // MVP: “rigenero” e segno stato.
        // In futuro: lanci job async / pipeline.
        s.setWebsiteStatus("GENERATED");
        s.setLastGeneratedAt(Instant.now());
        structures.save(s);

        return new WebsiteGenerationResponse(
                s.getWebsiteStatus(),
                "Sito rigenerato correttamente"
        );
    }

    // ----------------- helpers -----------------

    private StructureType parseType(String raw) {
        if (raw == null) throw new IllegalArgumentException("type required");
        try {
            return StructureType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid structure type: " + raw);
        }
    }

    private String ensureUniqueSlug(UUID accountId, String desired, UUID excludeId) {
        String base = desired;
        String slug = base;
        int i = 2;

        if (excludeId == null) {
            while (structures.existsByAccountIdAndSlug(accountId, slug)) {
                slug = base + "-" + i++;
            }
        } else {
            while (structures.existsByAccountIdAndSlugAndIdNot(accountId, slug, excludeId)) {
                slug = base + "-" + i++;
            }
        }
        return slug;
    }

    private String slugify(String input) {
        if (input == null) return "structure";
        String s = input.trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9\\s-]", "");
        s = s.replaceAll("\\s+", "-");
        s = s.replaceAll("-{2,}", "-");
        s = s.replaceAll("^-|-$", "");
        return s.isBlank() ? "structure" : s;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private StructureListItemResponse toListItem(Structure s) {
        return new StructureListItemResponse(
                s.getId(),                 // UUID (non .toString())
                s.getName(),
                s.getType().name(),
                s.getSlug(),
                s.isPublished(),
                s.isArchived(),

                s.getCity(),
                s.getCountry(),

                // website
                s.getDomain(),
                s.getWebsiteTemplate(),
                s.getWebsiteStatus(),
                0, // roomsCount TODO (poi lo calcoli)
                0  // membersCount TODO (poi lo calcoli)
        );
    }

    private StructureDetailResponse toDetail(Structure s) {
        return new StructureDetailResponse(
                s.getId(),                        // UUID id
                s.getAccount().getId(),            // UUID accountId

                s.getName(),                       // name
                s.getType().name(),                // type
                s.getSlug(),                       // slug

                s.getPhone(),
                s.getEmail(),
                s.getAddressLine(),
                s.getCity(),
                s.getPostalCode(),
                s.getCountry(),
                s.getTimezone(),
                s.getCurrency(),
                s.getCheckInTime(),
                s.getCheckOutTime(),

                s.isPublished(),
                s.isArchived(),

                s.getDomain(),
                s.getLogoUrl(),
                s.getCoverImageUrl(),
                s.getWebsiteTemplate(),
                s.getPrimaryColor(),
                s.getAccentColor(),
                s.getSeoTitle(),
                s.getSeoDescription(),

                s.getWebsiteStatus(),
                toOffset(s.getLastGeneratedAt()),
                toOffset(s.getCreatedAt()),
                toOffset(s.getUpdatedAt())
        );
    }


    private OffsetDateTime toOffset(Instant i) {
        return i == null ? null : i.atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime();
    }
}