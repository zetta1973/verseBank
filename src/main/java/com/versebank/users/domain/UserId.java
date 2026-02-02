package com.versebank.users.domain;

import java.util.Objects;
import java.util.UUID;

public record UserId(String value) {
    public UserId {
        Objects.requireNonNull(value, "UserId value cannot be null");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("UserId value cannot be empty");
        }
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
    
    public static UserId fromString(String value) {
        return new UserId(value);
    }
    
    public String getValue() {
        return value;
    }
}
