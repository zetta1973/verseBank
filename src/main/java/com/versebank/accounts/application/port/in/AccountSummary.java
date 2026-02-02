package com.versebank.accounts.application.port.in;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountSummary {
    private final String accountId;
    private final String customerId;
    private final String accountType; // Usamos String para simplificar la exportación desde el dominio
    private final BigDecimal balance;

    public AccountSummary(String accountId, String customerId, String accountType, BigDecimal balance) {
        this.accountId = Objects.requireNonNull(accountId);
        this.customerId = Objects.requireNonNull(customerId);
        this.accountType = Objects.requireNonNull(accountType);
        this.balance = Objects.requireNonNull(balance);
    }

    // Getters
    public String getAccountId() { return accountId; }
    public String getCustomerId() { return customerId; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSummary that = (AccountSummary) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
