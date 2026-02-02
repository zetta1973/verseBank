package com.versebank.accounts.domain;

import java.util.Objects;

public record CustomerId(String value) {
    
    public CustomerId {
        Objects.requireNonNull(value, "Customer ID cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        if (value.length() < 3) {
            throw new IllegalArgumentException("Customer ID must be at least 3 characters");
        }
        if (!value.matches("^[A-Za-z0-9_-]+$")) {
            throw new IllegalArgumentException("Customer ID can only contain letters, numbers, hyphens and underscores");
        }
    }
    
    public static CustomerId of(String value) {
        return new CustomerId(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}