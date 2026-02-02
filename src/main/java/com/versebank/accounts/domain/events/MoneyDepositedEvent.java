package com.versebank.accounts.domain.events;

import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.AccountId;

import java.time.LocalDateTime;

public class MoneyDepositedEvent extends DomainEvent {
    private final AccountId accountId;
    private final Balance amount;
    private final Balance newBalance;

    public MoneyDepositedEvent(AccountId accountId, Balance amount, Balance newBalance) {
        super("MoneyDeposited");
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public AccountId getAccountId() { return accountId; }
    public Balance getAmount() { return amount; }
    public Balance getNewBalance() { return newBalance; }
}