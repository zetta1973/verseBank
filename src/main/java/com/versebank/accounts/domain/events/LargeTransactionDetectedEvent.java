package com.versebank.accounts.domain.events;

import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.AccountId;

public class LargeTransactionDetectedEvent extends DomainEvent {
    private final AccountId accountId;
    private final Balance amount;
    private final String transactionType;

    public LargeTransactionDetectedEvent(AccountId accountId, Balance amount, String transactionType) {
        super("LargeTransactionDetected");
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public AccountId getAccountId() { return accountId; }
    public Balance getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
}