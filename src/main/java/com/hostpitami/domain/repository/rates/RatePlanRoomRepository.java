package com.hostpitami.domain.repository.rates;

import com.hostpitami.domain.entity.rates.RatePlanRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatePlanRoomRepository extends JpaRepository<RatePlanRoom, RatePlanRoom.Key> {
    List<RatePlanRoom> findByRatePlanId(UUID ratePlanId);
    void deleteByRatePlanId(UUID ratePlanId);
    boolean existsByRatePlanIdAndRoomId(UUID ratePlanId, UUID roomId);
}