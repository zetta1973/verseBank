package com.versebank.accounts.domain.valueobjects;

public enum AccountType {
    CHECKING("Checking"),
    SAVINGS("Savings"),
    BUSINESS("Business");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    public boolean allowsOverdraft() {
        return this == CHECKING || this == BUSINESS;
    }

    @Override
    public String toString() {
        return "AccountType{" + name() + " - " + description + "}";
    }
}