package com.hostpitami.service.account;

import com.hostpitami.domain.entity.account.*;
import com.hostpitami.domain.entity.auth.User;
import com.hostpitami.domain.repository.account.AccountMemberRepository;
import com.hostpitami.domain.repository.account.AccountRepository;
import com.hostpitami.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accounts;
    private final AccountMemberRepository members;
    private final UserRepository users;

    public AccountService(AccountRepository accounts, AccountMemberRepository members, UserRepository users) {
        this.accounts = accounts;
        this.members = members;
        this.users = users;
    }

    /**
     * Crea un nuovo Account per l'owner appena registrato + membership OWNER.
     * NB: Non crea la Structure (come da decisione).
     */
    @Transactional
    public Account createNewAccountForOwner(UUID ownerUserId) {
        User owner = users.findById(ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        Account a = new Account();
        a.setOwnerUser(owner);

        // nome di default: puoi cambiarlo dopo da dashboard
        a.setName(owner.getFullName() != null && !owner.getFullName().isBlank()
                ? owner.getFullName().trim()
                : "My Account");

        a.setType(AccountType.STANDARD);
        a.setPlan(PlanType.FREE);
        a.setPlanStatus(PlanStatus.NONE);

        Account saved = accounts.save(a);

        // Crea membership OWNER
        AccountMember m = new AccountMember(saved.getId(), owner.getId(), AccountMemberRole.OWNER);
        members.save(m);

        return saved;
    }
}
