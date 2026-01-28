package com.hostpitami.domain.repository.account;

import com.hostpitami.domain.entity.account.AccountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountMemberRepository extends JpaRepository<AccountMember, UUID> {

    Optional<AccountMember> findByAccountIdAndUserId(UUID accountId, UUID userId);

    List<AccountMember> findAllByUserId(UUID userId);

    boolean existsByAccountIdAndUserIdAndEnabledTrue(UUID accountId, UUID userId);

    @Query("""
        select am.accountId
        from AccountMember am
        where am.userId = :userId
          and am.enabled = true
        order by am.createdAt asc
    """)
    List<UUID> findAccountIdsForUser(UUID userId);
}