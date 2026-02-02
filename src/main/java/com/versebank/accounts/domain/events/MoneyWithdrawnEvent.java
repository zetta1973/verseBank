package com.versebank.accounts.domain.events;

import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.AccountId;

public class MoneyWithdrawnEvent extends DomainEvent {
    private final AccountId accountId;
    private final Balance amount;
    private final Balance newBalance;

    public MoneyWithdrawnEvent(AccountId accountId, Balance amount, Balance newBalance) {
        super("MoneyWithdrawn");
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public AccountId getAccountId() { return accountId; }
    public Balance getAmount() { return amount; }
    public Balance getNewBalance() { return newBalance; }
}