package com.hostpitami.domain.repository.account;

import com.hostpitami.domain.entity.account.Account;
import com.hostpitami.domain.entity.account.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    /**
     * TRUE se l’account ha selezionato un piano
     * (planStatus != NONE)
     */
    @Query("""
        select (count(a) > 0)
        from Account a
        where a.id = :accountId
          and a.planStatus <> :none
    """)
    boolean isPlanSelected(UUID accountId, PlanStatus none);

    /**
     * Recupera solo lo status piano
     */
    @Query("""
        select a.planStatus
        from Account a
        where a.id = :accountId
    """)
    Optional<PlanStatus> findPlanStatus(UUID accountId);

    boolean existsById(UUID accountId);
}

