package com.versebank.accounts.domain.valueobjects;

import java.math.BigDecimal;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;

public record Balance(BigDecimal amount) {
    
    public Balance {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be null or negative");
        }
    }

    public static Balance of(BigDecimal amount) {
        return new Balance(amount);
    }

    public static Balance zero() {
        return new Balance(BigDecimal.ZERO);
    }

    public Balance add(Balance other) {
        return new Balance(this.amount.add(other.amount));
    }

    public Balance subtract(Balance other) throws InsufficientFundsException {
        if (this.amount.compareTo(other.amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance: " + this.amount + " < " + other.amount);
        }
        return new Balance(this.amount.subtract(other.amount));
    }

    public boolean isGreaterThan(Balance other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Balance other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThanOrEqual(Balance other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return amount.compareTo(balance.amount) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(amount.setScale(2, java.math.RoundingMode.HALF_UP));
    }

    @Override
    public String toString() {
        return "Balance{" + amount.setScale(2, java.math.RoundingMode.HALF_UP) + "}";
    }
}
