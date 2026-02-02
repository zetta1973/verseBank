package com.versebank.accounts.domain.events;

import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.AccountId;

public class AccountOpenedEvent extends DomainEvent {
    private final AccountId accountId;
    private final String customerId;
    private final Balance initialBalance;

    public AccountOpenedEvent(AccountId accountId, String customerId, Balance initialBalance) {
        super("AccountOpened");
        this.accountId = accountId;
        this.customerId = customerId;
        this.initialBalance = initialBalance;
    }

    public AccountId getAccountId() { return accountId; }
    public String getCustomerId() { return customerId; }
    public Balance getInitialBalance() { return initialBalance; }
}