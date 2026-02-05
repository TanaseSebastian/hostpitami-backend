package com.hostpitami.service.account;

import com.hostpitami.domain.entity.account.Account;

import java.util.UUID;

public interface AccountService {
    Account createNewAccountForOwner(UUID ownerUserId);
}
